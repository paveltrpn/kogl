#!/bin/bash

shopt -s nullglob

# ========= kotlin compiler options ============================
KOTLIN_JVM_TARGET=21
KOTLIN_LANGUAGE_VERSION=2.2
KOTLIN_API_VERSION=2.2
#KOTLIN_VERBOSE=-verbose
# ==============================================================

# ========= test environment and set executables ===============
JAVA_BIN="java"
if [[ -n "$JAVA_HOME" ]]; then
    JAVA_BIN="$JAVA_HOME/bin/java"
    if [[ ! -x "$JAVA_BIN" ]]; then
        echo >&2 -e "'java' should be on the PATH or JAVA_HOME must point to a valid JDK installation"
        exit 1
    fi
fi

# Check all PATH
# ${PATH//:/ }; - replace colons with spaces to create a list.
for d in ${PATH//:/ }; do
  if [[ -x "$d/kotlinc" ]]; then
    KOTLIN_COMPILER_BIN="$d/kotlinc"
    break;
  fi
done

if [[ -z "$KOTLIN_COMPILER_BIN" ]]; then
    echo >&2 -e "'kotlinc' should be on the PATH."
    exit 1
fi
# ==============================================================

PROJECT=encode
OUT="../out"
DEP="../dep"

help() {
  echo -e "Options:"
  echo -e "\t-b --build: Build project, place *.class or *.jar files in out."
  echo -e "\t-r --run: Run project from *.class or *.jar files."
  echo -e "\t-a --all: Build and run."
  echo -e "\t-c --clean: Clean all build artifacts, completely delete out directory."
  echo -e "\tSet KOTLIN_VERBOSE in script source to use verbose kotlin compiler output."
}

cleanBuildArtifacts() {
    echo -e "=== clean $PROJECT ==="

    if [[ -d "$OUT" ]]; then
      rm -r $OUT/$PROJECT.jar
    fi
}

checkBuildDir() {
  if [[ ! -d "$OUT" ]]; then
    echo -e "=== $OUT does not exist. Create one ===\n"
    mkdir $OUT
  fi
}

build() {
    echo -e "=== build $PROJECT ===\n"
    echo -e "kotlin jvm target=$KOTLIN_JVM_TARGET"
    echo -e "kotlin compiler version=$KOTLIN_LANGUAGE_VERSION"
    echo -e "kotlin api version=$KOTLIN_API_VERSION\n"

    SOURCES=(
      ../encode/src/main/kotlin/*.kt
      ../modules/src/main/kotlin/algebra/*.kt
      ../modules/src/main/kotlin/canvas/*.kt
      ../modules/src/main/kotlin/image/*.kt
      ../modules/src/main/kotlin/film/*.kt
      ../modules/src/main/kotlin/spatial/*.kt
    )

    # echo -e "$PROJECT sources:"
    # for item in "${SOURCES[@]}"
    # do
    #   echo "$item"
    # done
    # echo ""

    DEPENDENCIES=(
      "$DEP/clikt-jvm-5.0.3.jar"
      "$DEP/okio-jvm-3.15.0.jar"
      "$DEP/kotlinx-coroutines-core-jvm-1.10.2.jar"
      "$DEP/kotlinx-serialization-core-jvm-1.9.0.jar"
      "$DEP/kotlinx-serialization-json-jvm-1.9.0.jar"
    )
    # create a space delimited string from array
    TMP=${DEPENDENCIES[*]}
    # use parameter expansion to substitute spaces with comma
    CP=${TMP// /:}

    echo -e "=== compile $PROJECT ===\n"
    "$KOTLIN_COMPILER_BIN" \
        -jvm-target $KOTLIN_JVM_TARGET \
        -language-version $KOTLIN_LANGUAGE_VERSION \
        -api-version $KOTLIN_API_VERSION \
        -include-runtime \
        $KOTLIN_VERBOSE \
        "${SOURCES[@]}" \
        -cp "$CP" \
        -d $OUT/$PROJECT.jar
}

run() {
    echo -e "=== run $PROJECT ===\n"

    if [[ ! -d "$OUT" ]]; then
      echo -e "=== $OUT does not exist. nothing to run ===\n"
      exit
    fi

    RUNTIME=(
      "$DEP/clikt-jvm-5.0.3.jar"
      "$DEP/okio-jvm-3.15.0.jar"
      "$DEP/kotlinx-coroutines-core-jvm-1.10.2.jar"
      "$DEP/kotlinx-serialization-core-jvm-1.9.0.jar"
      "$DEP/kotlinx-serialization-json-jvm-1.9.0.jar"
      "$OUT/$PROJECT.jar"
    )
    TMP=${RUNTIME[*]}
    CP=${TMP// /:}

    "$JAVA_BIN" -cp "${CP// /:}" \
        $PROJECT.MainKt
}

getopts ':-:brach' VAL
  case $VAL in
    b )
      checkBuildDir
      build
      exit
      ;;
#    r ) OFILE="$OPTARG" ;;
    r )
      run
      exit
      ;;
    a )
      build
      run
      exit
      ;;
    c )
      cleanBuildArtifacts
      exit
      ;;
    h )
      help
      exit
      ;;
    - )
      case $OPTARG in
        build )
          checkBuildDir
          build
          exit
          ;;
        run )
          run
          exit
          ;;
        all )
          build
          run
          exit
          ;;
        clean )
          cleanBuildArtifacts
          exit
          ;;
        help )
          help
          exit
          ;;
        * )
          echo "unknown long argument: $OPTARG"
          exit
          ;;
      esac
      ;;
  #--------------------------------------------------------
    : )
      echo "error: no argument supplied"
      ;;
    * )
      echo "error: unknown option $OPTARG"
      echo " valid options are: brach"
      ;;
  esac
shift $((OPTIND -1))
