/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.ml.hmm.distributions;

import java.util.Arrays;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataPair;

public class DiscreteDistribution implements StateDistribution {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final double[] probabilities;

	public DiscreteDistribution(final double[] probabilities) {
		if (probabilities.length == 0) {
			throw new IllegalArgumentException("Invalid empty array");
		}

		this.probabilities = new double[probabilities.length];

		for (int i = 0; i < probabilities.length; i++) {
			if ((this.probabilities[i] = probabilities[i]) < 0.) {
				throw new IllegalArgumentException();
			}
		}
	}

	public DiscreteDistribution(final int c) {
		this.probabilities = new double[c];

		for (int i = 0; i < c; i++) {
			this.probabilities[i] = 1.0 / c;
		}
	}

	@Override
	public DiscreteDistribution clone() {
		try {
			return (DiscreteDistribution) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public void fit(final MLDataSet co) {
		if (co.size() < 1) {
			throw new IllegalArgumentException("Empty observation set");
		}

		for (int i = 0; i < this.probabilities.length; i++) {
			this.probabilities[i] = 0.0;
		}

		for (final MLDataPair o : co) {
			this.probabilities[(int) o.getInput().getData(0)]++;
		}

		for (int i = 0; i < this.probabilities.length; i++) {
			this.probabilities[i] /= co.size();
		}
	}

	@Override
	public void fit(final MLDataSet co, final double[] weights) {
		if ((co.size() < 1) || (co.size() != weights.length)) {
			throw new IllegalArgumentException();
		}

		Arrays.fill(this.probabilities, 0.0);

		int i = 0;
		for (final MLDataPair o : co) {
			this.probabilities[(int) o.getInput().getData(0)] += weights[i++];
		}
	}

	@Override
	public MLDataPair generate() {
		final MLData result = new BasicMLData(1);
		double rand = Math.random();

		for (int i = 0; i < (this.probabilities.length - 1); i++) {
			if ((rand -= this.probabilities[i]) < 0.0) {
				result.setData(0, i);
				return new BasicMLDataPair(result);
			}
		}

		result.setData(0, this.probabilities.length - 1);
		return new BasicMLDataPair(result);
	}

	@Override
	public double probability(final MLDataPair o) {
		if (o.getInput().getData(0) > (this.probabilities.length - 1)) {
			throw new IllegalArgumentException("Wrong observation value");
		}

		return this.probabilities[(int) o.getInput().getData(0)];
	}

}