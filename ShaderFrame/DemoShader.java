import static java.lang.Math.abs;
import static java.lang.Math.min;

public class DemoShader extends Shader {

	void shader(double[] p, double u, double v, double t) {

		double r, g, b, waterPixelValue, skyPixelValue, waterLineValue;

		//för att solnedgången ska hamna ovanför och inte under
		double inv_v = (1.0 - v);

		//Skapa noise med med avlånga wiggles för att ge  en känsla av horsisont-perspektiv'
		//  v adderas för att skapa intesitet närmast "solen"
		waterPixelValue = inv_v +  0.5 * ImprovedNoise.noise(inv_v*16.0, u*6.0 + t*0.1,t*0.4);

		// annan noise-funktion ovanför vattenlinjen
		// skiljelinjen är också en noise-funktion
		skyPixelValue = v + 1.5*PerlinSimplexNoise.noise(v*4.0, u*2.0,t*0.01);
		waterLineValue = (0.25+ 0.001*ImprovedNoise.noise(u*32.0, v*16.0,t*0.4));

		// sinuskurva, med hög intesitet i mitten. Tänk en kulle/berg
		double sunReflectionFactor = 0.4*(Math.sin(2*Math.PI * ((u) - .25)) + 1.0) + 0.1;

		//smalnar av sinuskruvan lite
		sunReflectionFactor = 50 * Math.pow(0.2 * sunReflectionFactor, 2.0);

		//multiplicera sinusfunktionen för att skenet bara ska vara i mitten ("strimman" från solen)
		waterPixelValue = waterPixelValue*sunReflectionFactor + 0.3 * v;


		//om vatten (if-else bör ersättas av ngt annat, exempelvis step-funktion)
		if(v > waterLineValue){
			//få röda färger i vatten från solen
			r = waterPixelValue*2;
			g = waterPixelValue*2-0.5;
			b = (waterPixelValue*5-3.5) * 0.9;

			//göra det mörka vattnet lite blått
			double colorIntensity = (r + g + b) * 0.33;
			b = 0.1 * Math.pow(1 - colorIntensity, 2);
		}
		else{  //om himmel
			r = 0.3;
			g = 0.15;
			b = 0.1;

			//moln
			r += Math.max(0.0,skyPixelValue);
			g += Math.max(0.0,skyPixelValue);
			b += Math.max(0.0,skyPixelValue);

			//cirkelns ekvation (x - 0.5)^2 + (y - 0.5)^2 = r^2.
			if(Math.pow((u - 0.5), 2.0) + Math.pow((v - 0.2), 2.0) < 0.035){
				//solen
				r += 1.0;
				g += 1.0;
				b += 0.0;
			}
		}

		p[0]= r;
		p[1]= g;
		p[2]= b;

	}

}
