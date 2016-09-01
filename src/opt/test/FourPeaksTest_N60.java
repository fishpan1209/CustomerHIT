package opt.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.SingleCrossOver;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

public class FourPeaksTest_N60 {
	

	public static void main(String[] args) {
		ArrayList<Double> RHC_optimal= new ArrayList();
		ArrayList<Double> RHC_runtime = new ArrayList();
		ArrayList<Double> SAA_optimal= new ArrayList();
		ArrayList<Double> SAA_runtime = new ArrayList();
		ArrayList<Double> GA_optimal= new ArrayList();
		ArrayList<Double> GA_runtime = new ArrayList();
		ArrayList<Double> MIMIC_optimal= new ArrayList();
		ArrayList<Double> MIMIC_runtime = new ArrayList();
		int size = 60;
		for (int N = 60; N <= size; N += 10) {
			int T = N / 10;
			int[] ranges = new int[N];
			Arrays.fill(ranges, 2);
			EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
			Distribution odd = new DiscreteUniformDistribution(ranges);
			NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
			MutationFunction mf = new DiscreteChangeOneMutation(ranges);
			CrossoverFunction cf = new SingleCrossOver();
			Distribution df = new DiscreteDependencyTree(.1, ranges);
			HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd,
					nf);
			GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(
					ef, odd, mf, cf);
			ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(
					ef, odd, df);
			long start;

			System.out
					.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
							+ "\n"
							+ "Optimizing FourPeaks Problem, "
							+ "Sample Size N="
							+ N
							+ "\n"
							+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

			// RHC

			for (int i = 1; i < 20; i++) {
				int iter = (int) Math.pow(2, i);
				System.out.println("\n" + "RHC: ");
				for (int j = 1; j < 11; j++) {

					start = System.currentTimeMillis();
					RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
					FixedIterationTrainer fit = new FixedIterationTrainer(rhc,
							iter);
					fit.train();
					double runtime = System.currentTimeMillis() - start;
					double optimal = ef.value(rhc.getOptimal());
					System.out.println("Sample Size N=" + N + "   Iteration="
							+ iter + "   Round=" + j + "   Optimal: "
							+ optimal + "   " + "Runtime: "
							+ runtime + "ms");
					RHC_optimal.add(optimal);
					RHC_runtime.add(runtime);

				}
			}
        
			// SAA
			for (int i = 1; i < 20; i++) {
				int iter = (int) Math.pow(2, i);
				System.out.println("\n" + "SAA: ");
				for (int j = 1; j < 11; j++) {
					start = System.currentTimeMillis();
					SimulatedAnnealing sa = new SimulatedAnnealing(100, .95,
							hcp);
					FixedIterationTrainer fit = new FixedIterationTrainer(sa,
							iter);
					fit.train();
					double runtime = System.currentTimeMillis() - start;
					double optimal = ef.value(sa.getOptimal());
					System.out.println("Sample Size N=" + N + "   Iteration="
							+ iter + "   Round=" + j + "   Optimal: " + optimal
							+ "   " + "Runtime: " + (runtime) + "ms");
					SAA_optimal.add(optimal);
					SAA_runtime.add(runtime);

				}
			}

			// GA
			for (int i = 1; i < 20; i++) {
				int iter = (int) Math.pow(2, i);
				System.out.println("\n" + "GA: ");
				for (int j = 1; j < 11; j++) {
					start = System.currentTimeMillis();
					StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(
							200, 100, 20, gap);
					FixedIterationTrainer fit = new FixedIterationTrainer(ga,
							iter);
					fit.train();
					double runtime = System.currentTimeMillis() - start;
					double optimal = ef.value(ga.getOptimal());
					System.out.println("Sample Size N=" + N + "   Iteration="
							+ iter + "   Round=" + j + "   Optimal: " + optimal
							+ "   " + "Runtime: " + (runtime) + "ms");
					GA_optimal.add(optimal);
					GA_runtime.add(runtime);

				}
			}

			// MIMIC
			for (int i = 1; i < 20; i++) {
				int iter = (int) Math.pow(2, i);
				System.out.println("\n" + "MIMIC: ");
				for (int j = 1; j < 11; j++) {
					start = System.currentTimeMillis();
					MIMIC mimic = new MIMIC(200, 5, pop);
					FixedIterationTrainer fit = new FixedIterationTrainer(
							mimic, iter);
					fit.train();
					double runtime = System.currentTimeMillis() - start;
					double optimal = ef.value(mimic.getOptimal());
					System.out.println("Sample Size N=" + N + "   Iteration="
							+ iter + "   Round=" + j + "   Optimal: " + optimal
							+ "   " + "Runtime: "
							+ (System.currentTimeMillis() - start) + "ms");
					MIMIC_optimal.add(optimal);
					MIMIC_runtime.add(runtime);
				}
			}

		}
		// write RHC result to txt
		try {
			PrintWriter results = new PrintWriter("FourPeaks_RHC_60.txt");
			results.println("N" + "," + "Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			int k=0;
			for (int n = 60; n <= size; n += 10) {
				for (int i = 1; i < 20; i++) {
					int iter = (int) Math.pow(2, i);

					for (int j = 1; j < 11; j++) {
						
						results.println(n + "," + iter + "," + j + ","
								+ RHC_optimal.get(k) + "," + RHC_runtime.get(k));
						k++;

					}
				}
			}

			results.close();
		} catch (IOException ex) {
			System.out.println("Unable to create results file:");
			System.out.println(ex);
		}
		// write SAA result to txt
		try {
			PrintWriter results = new PrintWriter("FourPeaks_SAA_60.txt");
			results.println("N" + "," + "Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			int k=0;
			for (int n = 60; n <= size; n += 10) {
				for (int i = 1; i < 20; i++) {
					int iter = (int) Math.pow(2, i);

					for (int j = 1; j < 11; j++) {
						
						results.println(n + "," + iter + "," + j + ","
								+ SAA_optimal.get(k) + "," + SAA_runtime.get(k));
						k++;

					}
				}
			}

			results.close();
		} catch (IOException ex) {
			System.out.println("Unable to create results file:");
			System.out.println(ex);
		}

		// write GA result to txt
		try {
			PrintWriter results = new PrintWriter("FourPeaks_GA_60.txt");
			results.println("N" + "," + "Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			int k=0;
			for (int n = 60; n <= size; n += 10) {
				for (int i = 1; i < 20; i++) {
					int iter = (int) Math.pow(2, i);

					for (int j = 1; j < 11; j++) {
						
						results.println(n + "," + iter + "," + j + ","
								+ GA_optimal.get(k) + "," + GA_runtime.get(k));
						k++;

					}
				}
			}

			results.close();
		} catch (IOException ex) {
			System.out.println("Unable to create results file:");
			System.out.println(ex);
		}

		// write MIMIC result to txt
		try {
			PrintWriter results = new PrintWriter("FourPeaks_MIMIC_60.txt");
			results.println("N" + "," + "Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			int k=0;
			for (int n = 60; n <= size; n += 10) {
				for (int i = 1; i < 20; i++) {
					int iter = (int) Math.pow(2, i);

					for (int j = 1; j < 11; j++) {
						
						results.println(n + "," + iter + "," + j + ","
								+ MIMIC_optimal.get(k) + ","
								+ MIMIC_runtime.get(k));
						k++;

					}
				}
			}

			results.close();
		} catch (IOException ex) {
			System.out.println("Unable to create results file:");
			System.out.println(ex);
		}

	}
}
