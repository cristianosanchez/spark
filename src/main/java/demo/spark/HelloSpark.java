package demo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloSpark {

	private static Logger logger = LoggerFactory.getLogger(HelloSpark.class);

	public static void main(String[] args) {

		if (args.length < 1) {
			logger.error("missing parameter: file path");
		}

		String logFile = args[0];
		logger.debug(String.format("Using file %s", logFile));
		
		SparkConf conf = new SparkConf().setAppName("Simple Application");
		JavaSparkContext sc = new JavaSparkContext(conf);
		//JavaRDD<String> logData = sc.textFile(logFile).cache();
		JavaRDD<String> logData = sc.textFile(logFile);
		
		logger.debug(String.format("Number of lines %d", logData.count()));
		
		long numAs = logData.filter(new Function<String, Boolean>() {
			public Boolean call(String s) {
				return s.contains("a");
			}
		}).count();

		long numBs = logData.filter(new Function<String, Boolean>() {
			public Boolean call(String s) {
				return s.contains("b");
			}
		}).count();

		logger.info("Lines with a: " + numAs + ", lines with b: " + numBs);
	}
}