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

PROJECT=tire
OUT="out"
DEP="dep"

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

SOURCES=(
  modules/src/main/kotlin/algebra/*.kt
  modules/src/main/kotlin/image/*.kt
  modules/src/main/kotlin/spatial/*.kt
  modules/src/main/kotlin/context/*.kt
  modules/src/main/kotlin/render/*.kt
  modules/src/main/kotlin/config/*.kt
  modules/src/main/kotlin/scene/*.kt
  apps/tire/src/main/kotlin/tire/*.kt
)

DEPENDENCIES=(
  "$DEP/clikt-jvm-5.0.3.jar"
  "$DEP/okio-jvm-3.15.0.jar"
  "$DEP/kotlinx-coroutines-core-jvm-1.10.2.jar"
  "$DEP/kotlinx-serialization-core-jvm-1.9.0.jar"
  "$DEP/kotlinx-serialization-json-jvm-1.9.0.jar"
  "$DEP/lwjgl.jar"
  "$DEP/lwjgl-javadoc.jar"
  "$DEP/lwjgl-natives-linux.jar"
  "$DEP/lwjgl-sources.jar"
  "$DEP/lwjgl-glfw.jar"
  "$DEP/lwjgl-glfw-javadoc.jar"
  "$DEP/lwjgl-glfw-natives-linux.jar"
  "$DEP/lwjgl-glfw-sources.jar"
  "$DEP/lwjgl-shaderc.jar"
  "$DEP/lwjgl-shaderc-javadoc.jar"
  "$DEP/lwjgl-shaderc-natives-linux.jar"
  "$DEP/lwjgl-shaderc-sources.jar"
  "$DEP/lwjgl-spvc.jar"
  "$DEP/lwjgl-spvc-javadoc.jar"
  "$DEP/lwjgl-spvc-natives-linux.jar"
  "$DEP/lwjgl-spvc-sources.jar"
  "$DEP/lwjgl-vma.jar"
  "$DEP/lwjgl-vma-javadoc.jar"
  "$DEP/lwjgl-vma-natives-linux.jar"
  "$DEP/lwjgl-vma-sources.jar"
  "$DEP/lwjgl-vulkan.jar"
  "$DEP/lwjgl-unsafe.jar"
  "$DEP/lwjgl-unsafe-sources.jar"
  "$DEP/lwjgl-vulkan.jar"
  "$DEP/lwjgl-vulkan-javadoc.jar"
  "$DEP/lwjgl-vulkan-sources.jar"
)

build() {
    echo -e "=== compile $PROJECT ===\n"

#    echo -e "kotlin compiler bin: $KOTLIN_COMPILER_BIN"
#    $KOTLIN_COMPILER_BIN -version
#    echo -e "java bin: $JAVA_BIN"
#    $JAVA_BIN -version
#    echo -e ""

    echo -e "jvm target version=$KOTLIN_JVM_TARGET"
    echo -e "kotlin target language version=$KOTLIN_LANGUAGE_VERSION\n"

#    echo -e "$PROJECT sources:"
#    for item in "${SOURCES[@]}"
#    do
#      echo "$item"
#    done
#    echo ""

    # create a space delimited string from array
    TMP=${DEPENDENCIES[*]}
    # use parameter expansion to substitute spaces with comma
    CP=${TMP// /:}

    $KOTLIN_COMPILER_BIN \
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
      ${DEPENDENCIES[*]}
      "$OUT/$PROJECT.jar"
    )

    TMP=${RUNTIME[*]}
    CP=${TMP// /:}

    # Convenient for Renderdoc
    # echo "$CP"

    $JAVA_BIN -cp "${CP// /:}" \
        $PROJECT.MainKt
}

while getopts ':-:brac' VAL ; do
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
      echo " valid options are: aov"
      ;;
  esac
done
shift $((OPTIND -1))
