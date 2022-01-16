attribute vec4 a_Position;
attribute vec4 a_Color;

// varying变量可以在片段着色器使用
varying vec4 v_Color;
void main()
{
    v_Color=a_Color;
    gl_Position = a_Position;
    gl_PointSize = 10.0;
}

