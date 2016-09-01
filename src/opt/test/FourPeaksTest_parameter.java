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

public class FourPeaksTest_parameter {
	

	public static void main(String[] args) {
		
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

	

			// MIMIC
			for(int samples = 100; samples<600; samples+=100){
			
			for (int i = 2; i < 15; i++) {
				int iter = (int) Math.pow(2, i);
				
				for (int j = 1; j < 11; j++) {
					System.out.println("\nsamples: : "+samples+"  Iteration: "+iter+"  round: "+j);
	
					start = System.currentTimeMillis();
					MIMIC mimic = new MIMIC(samples, 5, pop);
					FixedIterationTrainer fit = new FixedIterationTrainer(
							mimic, iter);
					fit.train();
					double runtime = System.currentTimeMillis() - start;
					double optimal = ef.value(mimic.getOptimal());
					System.out.println("Optimal: " + optimal
							+ "   " + "Runtime: "
							+ (System.currentTimeMillis() - start) + "ms");
					MIMIC_optimal.add(optimal);
					MIMIC_runtime.add(runtime);
				}
			}

		}
		
		// write MIMIC result to txt
		try {
			PrintWriter results = new PrintWriter("FourPeaks_MIMIC_60_samples.txt");
			results.println("samples" + "," + "Iteration" + "," + "Round" + ","
					+ "Optimal" + "," + "Runtime");
			int k=0;
			for(int samples =100; samples<600;samples+=100){
				for (int i = 2; i < 15; i++) {
					int iter = (int) Math.pow(2, i);

					for (int j = 1; j < 11; j++) {
						
						results.println(samples + "," + iter + "," + j + ","
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
}

