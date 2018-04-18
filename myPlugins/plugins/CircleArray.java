/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/

/**
 * This class is not thread-safe for performance.
 *  Each thread must handle only one instance of this class
 *  Be care when using it in multi-threaded context
 *  The datas are a tuple ( Long, Double) where
 *  the key Long must be naturally sorted => ex Timestamps
 */
package plugins;

// TODO: Auto-generated Javadoc
/**
 * The Class CircleArray.
 */
public class CircleArray {
	
	/** The taille. */
	public int taille = 10;
	
	/** The array. */
	public StrucLongDouble[] array;
	
	/** The pos. */
	public int pos = 0;
	
	/** The sum. */
	public Double sum = 0.0;
	
	/** The throughput. */
	public Double throughput = 0.0;
	
	/** The avg. */
	public Double avg = 0.0;
	
	/** The max. */
	public Double max = 0.0;
	
	/** The freq. */
	public Double freq = 0.0;
	
	/** The period. */
	public Double period = 0.0;

	/**
	 * Instantiates a new circle array.
	 *
	 * @param taille the taille
	 */
	public CircleArray(int taille) {
		super();
		pos=1;
		this.taille = taille;
		if (this.taille < 2)
			this.taille = 2;
		array = new StrucLongDouble[this.taille];

		for (int i = 0; i < taille; i++) {
			array[i] = new StrucLongDouble(0L, 0.0);
		}

	}

	/**
	 * Put.
	 *
	 * @param tup the tup
	 */
	public void put(StrucLongDouble tup) {
		array[pos] = tup;
		fill();
		pos = (pos + 1) % taille;
	}

	/**
	 * Fill.
	 */
	public void fill() {
		sum = 0.0;
		for (int i = 0; i < taille; i++) {
			sum += array[pos].value;

		}

		avg = sum / taille;

		long diffAbs = (array[pos].time) - array[(pos + 1) % taille].time;

		max = 0.0;
		// a reactiver si besoin
		// array foreach ( tup => if (tup ._2 > max ) max=tup._2)
		if (diffAbs == 0) {
			throughput = 0.0;
			freq = 0.0;
			period = 0.0;
		} else {
			throughput = sum / (double) diffAbs;
			// Frequence in hertz 1/ms in (taille -1) intervals
			period = (double) diffAbs / (double) (taille - 1);
			freq = 1.0 / period;

		}
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]) {
		CircleArray crcarr = new CircleArray(Integer.parseInt(args[0]));
		Long deb = System.currentTimeMillis();
		for (int i = 0; i < Integer.parseInt(args[0]); i++) {
			crcarr.put(new StrucLongDouble((long) i, (double) 1000 * i));
		}
		System.out.println("array=");
		for (StrucLongDouble tup : crcarr.array) {
			System.out.println(tup.value);
		}
		System.out.println("crcarr.sum =" + crcarr.sum);
		System.out.println("crcarr.slope =" + crcarr.throughput);
		System.out.println("duration =" + (System.currentTimeMillis() - deb));
	}

}
