package Classifier;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.*;
import java.util.regex.*;

import cc.mallet.classify.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.types.*;
import cc.mallet.util.Randoms;

public class MyNaiveBayes implements Serializable {
	//Train a classifier
	public static Classifier trainClassifier(InstanceList trainingInstances) {
		// Here we use a maximum entropy (ie polytomous logistic regression) classifier.
		ClassifierTrainer trainer = new NaiveBayesTrainer();
		return trainer.train(trainingInstances);
	}

	//save a trained classifier/write a trained classifier to disk
	public void saveClassifier(Classifier classifier, String savePath) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(savePath));
		oos.writeObject(classifier);
		oos.flush();
		oos.close();
	}

	//restore a saved classifier
	public Classifier loadClassifier(String savedPath) throws FileNotFoundException, IOException, ClassNotFoundException {
		// Here we load a serialized classifier from a file.
		Classifier classifier;
		ObjectInputStream ois = new ObjectInputStream (new FileInputStream (new File(savedPath)));
		classifier = (Classifier) ois.readObject();
		ois.close();
		return classifier;
	}

	//predict & evaluate
	public String predict(Classifier classifier, Instance testInstance) {
		Labeling labeling = classifier.classify(testInstance).getLabeling();
		Label label = labeling.getBestLabel();
		return (String)label.getEntry();
	}

	public void evaluate(Classifier classifier, String testFilePath) throws IOException {
		InstanceList testInstances = new InstanceList(classifier.getInstancePipe());

		//format of input data:[name] [label] [data ... ]
		CsvIterator reader = new CsvIterator(new FileReader(new File(testFilePath)), "(\\w+)\\s+(\\w+)\\s+(.*)", 3, 2, 1); // (data, label, name) field indices

		// Add all instances loaded by the iterator to our instance list
		testInstances.addThruPipe(reader);
		Trial trial = new Trial(classifier, testInstances);

		//evaluation metrics.precision, recall, and F1
		// System.out.println("Accuracy: " + trial.getAccuracy());
		// System.out.println("F1 for class 'good': " + trial.getF1("good"));
		//System.out.println("Precision for class '" +classifier.getLabelAlphabet().lookupLabel(1) + "': " +trial.getPrecision(1));
	}

	//perform n-fold cross validation
	public static Trial testTrainSplit(MaxEntTrainer trainer, InstanceList instances) {
		int TRAINING = 0;
		int TESTING = 1;
		int VALIDATION = 2;

		// Split the input list into training (90%) and testing (10%) lists.
		InstanceList[] instanceLists = instances.split(new Randoms(), new double[] {0.9, 0.1, 0.0});
		Classifier classifier = trainClassifier(instanceLists[TRAINING]);
		return new Trial(classifier, instanceLists[TESTING]);
	}

	Pipe pipe;

	public MyNaiveBayes() {
		pipe = buildPipe();
	}

	public Pipe buildPipe() {
		ArrayList pipeList = new ArrayList();

		// Read data from File objects
		pipeList.add(new Input2CharSequence("UTF-8"));

		// Regular expression for what constitutes a token.
		//  This pattern includes Unicode letters, Unicode numbers,
		//   and the underscore character. Alternatives:
		//    "\\S+"   (anything not whitespace)
		//    "\\w+"    ( A-Z, a-z, 0-9, _ )
		//    "[\\p{L}\\p{N}_]+|[\\p{P}]+"   (a group of only letters and numbers OR
		//                                    a group of only punctuation marks)
		Pattern tokenPattern =
		    Pattern.compile("[\\p{L}\\p{N}_]+");

		// Tokenize raw strings
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));

		// Normalize all tokens to all lowercase
		pipeList.add(new TokenSequenceLowercase());

		// Remove stopwords from a standard English stoplist.
		//  options: [case sensitive] [mark deletions]
		pipeList.add(new TokenSequenceRemoveStopwords(false, false));

		// Rather than storing tokens as strings, convert
		//  them to integers by looking them up in an alphabet.
		pipeList.add(new TokenSequence2FeatureSequence());

		// Do the same thing for the "target" field:
		//  convert a class label string to a Label object,
		//  which has an index in a Label alphabet.
		pipeList.add(new Target2Label());

		// Now convert the sequence of features to a sparse vector,
		//  mapping feature IDs to counts.
		pipeList.add(new FeatureSequence2FeatureVector());

		// Print out the features and the label
		pipeList.add(new PrintInputAndTarget());

		return new SerialPipes(pipeList);
	}

	public InstanceList readDirectory(File directory) {

		return readDirectories(new File[] {directory});
	}

	public InstanceList readDirectories(File[] directories) {

		FileIterator iterator =
		    new FileIterator(directories, new TxtFilter(),
		                     FileIterator.LAST_DIRECTORY);

		InstanceList instances = new InstanceList(pipe);

		instances.addThruPipe(iterator);

		return instances;


	}

	/** This class illustrates how to build a simple file filter */
	class TxtFilter implements FileFilter {

		/** Test whether the string representation of the file
		 *   ends with the correct extension. Note that {@ref FileIterator}
		 *   will only call this filter if the file is not a directory,
		 *   so we do not need to test that it is a file.
		 */
		public boolean accept(File file) {
			//return file.toString().endsWith(".html");
			return file.toString().contains(".html");
		}
	}

	static public void main (String[] args) {
		System.out.println();
		//File file=new File("doc/train");
		MyNaiveBayes naivebays = new MyNaiveBayes();
		InstanceList instances = naivebays.readDirectory(new File("doc/train")); //训练集
		Classifier maxentclassifier = naivebays.trainClassifier(instances);
		InstanceList test = naivebays.readDirectory(new File("doc/check"));//测试集
		for (int i = 0; i < test.size(); i++) {
			Instance testinstance = test.get(i);
			System.out.println(i + ":" + naivebays.predict(maxentclassifier, testinstance));
		}

	}
}
