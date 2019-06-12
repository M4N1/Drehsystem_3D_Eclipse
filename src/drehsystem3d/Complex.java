package drehsystem3d;

class Complex
{
  float re;
  float im;
  float freq;
  float amp;
  float phase;


  Complex(float r, float i)
  {
    re = r;
    im = i;
  }

  Complex()
  {
    re = 0;
    im = 0;
  }

  Complex(float r, float i, float f, float a, float p)
  {
    re = r;
    im = i;
    freq = f;
    amp = a;
    phase = p;
  }

  Complex mult(Complex other)
  {
    float rea = re * other.re - im * other.im;
    float ima = re * other.im + im * other.re;
    return new Complex(rea, ima);
  }

  Complex add(Complex other) 
  {
    return new Complex(re + other.re, im + other.im);
  }

  Complex sub(Complex other) 
  {
    return new Complex(re - other.re, im - other.im);
  }

  Complex pow(int n)
  {
    Complex result = this; 
    for (int i = 0; i < n; i++)
    {
      result = result.mult(this);
    }
    return result;
  }

  Complex copy()
  {
    float r = re;
    float i = im;
    return new Complex(r, i);
  }

  Complex mult(float mult)
  {
    re *= Math.sqrt(mult);
    im *= Math.sqrt(mult);
    return new Complex(re, im);
  }

  Complex div(float divisor)
  {
    float r = re / divisor;
    float i = im / divisor;
    return new Complex(r, i);
  }

  void normalize()
  {
    re /= mag();
    im /= mag();
  }

  float heading()
  {
    return (float)Math.atan2(im, re);
  }

  void setMag(float r)
  {
    this.normalize();
    this.mult(r);
  }

  float mag()
  {
    return (float)Math.sqrt(re * re + im * im);
  }

  float magSq()
  {
    return re * re + im * im;
  }

  void rotate(float theta)
  {
    re += Math.cos(theta);
    im += Math.sin(theta);
  }
  
  @Override
	public String toString()
	{
		return 		"[re:"		+ this.re
				+	", im:"		+ this.im
				+	", freq:"	+ this.freq
				+	", amp:"	+ this.amp
				+	", phase:"	+ this.phase
				+	"]";
	}
}


class RandomComplex extends Complex
{
  RandomComplex()
  {
    re = (float)Math.random()*2-1;
    im = (float)Math.random()*2-1;
    normalize();
  }
}

class ComplexFromAngle extends Complex
{
  ComplexFromAngle(float theta)
  {
    re = (float)Math.sin(theta);
    im = (float)Math.cos(theta);
    normalize();
  }
}
