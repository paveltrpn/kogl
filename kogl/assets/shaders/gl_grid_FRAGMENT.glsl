in vec3 vWorldPos;
out vec4 outFragColor;

uniform float gridSize;
uniform float lineThickness;
uniform float maxRange;
uniform float zoomSensitivity;
uniform vec3 colorMajor; // Color for lines at multiples of (gridSize * 5) e.g., 100m
uniform vec3 colorMinor; // Color for standard lines e.g., 10m
uniform float majorDivisor; // How often a major line appears (e.g., 5)

void main() {
    //    vec3 CB_cameraPos = vec3(0.0, 1.0, 1.0);
    vec3 CB_cameraPos = vec3(-18.0, -13.0, -14.0);

    // 1. Early Z-culling based on distance
    float distToCamera = length(vWorldPos - CB_cameraPos);
    if (distToCamera > maxRange) {
        discard;
    }

    // 2. Calculate local coordinates relative to the grid size
    // We use absolute values for symmetry, but mod handles negative coords naturally too
    float xLocal = mod(vWorldPos.x, gridSize);
    float yLocal = mod(vWorldPos.y, gridSize);

    // 3. Determine distance to the nearest vertical and horizontal lines
    // The distance to the nearest line is the minimum of (xLocal, gridSize - xLocal)
    float distToXLine = min(xLocal, gridSize - xLocal);
    float distToYLine = min(yLocal, gridSize - yLocal);

    // 4. Determine if we are on a line
    float alpha = 0.0;

    // Check X-axis lines
    if (distToXLine < lineThickness) {
        // Check if this is a Major Line (e.g., every 5th line)
        // We check the integer quotient to see if it's a multiple
        float gridIndexX = round(vWorldPos.x / gridSize);
        bool isMajorX = (mod(gridIndexX, majorDivisor) == 0.0);

        alpha = 1.0;

        // Mix colors if it's a major line
        if (isMajorX) {
            outFragColor = vec4(colorMajor, 1.0);
        } else {
            outFragColor = vec4(colorMinor, 1.0);
        }
    }
    // Check Y-axis lines (only if we weren't already on an X line)
    else if (distToYLine < lineThickness) {
        float gridIndexY = round(vWorldPos.y / gridSize);
        bool isMajorY = (mod(gridIndexY, majorDivisor) == 0.0);

        alpha = 1.0;

        if (isMajorY) {
            outFragColor = vec4(colorMajor, 1.0);
        } else {
            outFragColor = vec4(colorMinor, 1.0);
        }
    }

    // 5. Fade out lines based on distance (Atmospheric Perspective / Fog)
    // Calculate fade factor: 1.0 at camera, 0.0 at maxRange
    float fade = 1.0 - (distToCamera / maxRange);
    // Apply a curve to the fade for smoother disappearance
    fade = clamp(pow(fade, 2.0), 0.0, 1.0);

    // 6. Zoom-based Line Thickness Adjustment
    // If camera zooms out (far away), lines get thinner visually.
    // To compensate, we scale the thickness by camera distance.
    // Note: This creates the "Infinite" look where lines stay constant width relative to scene size.
    float zoomScale = mix(1.0, distToCamera * 0.01, zoomSensitivity);

    // If the adjusted line is now too thin, fade it out gently
    float adjustedThickness = lineThickness * zoomScale;
    if (adjustedThickness < 0.1) {
        fade *= (adjustedThickness / 0.1);
    }

    if (alpha < 0.5) {
        discard;
    }

    outFragColor.a *= fade;
}