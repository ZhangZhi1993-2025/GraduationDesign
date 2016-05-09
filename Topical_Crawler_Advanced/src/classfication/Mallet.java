package classfication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.*;

import cc.mallet.classify.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.types.*;
import cc.mallet.util.Randoms;

public class Mallet implements Serializable {
	//Train a classifier
    public static Classifier trainClassifier(InstanceList trainingInstances) {
        // Here we use a maximum entropy (ie polytomous logistic regression) classifier.训练，贝叶斯分类器                                               
        ClassifierTrainer trainer = new NaiveBayesTrainer();
        return trainer.train(trainingInstances);
    }
    
    //save a trained classifier/write a trained classifier to disk
    public void saveClassifier(Classifier classifier,String savePath) throws IOException{
        ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(savePath));
        oos.writeObject(classifier);
        oos.flush();
        oos.close();        
    }
    
    //restore a saved classifier
    public Classifier loadClassifier(String savedPath) throws FileNotFoundException, IOException, ClassNotFoundException{                                              
        // Here we load a serialized classifier from a file.恢复已经保存的分类
    	//用标准的方式来保存分类和马利特数据
    	//重复使用是通过Java序列化。
    	//下面我们从文件中加载序列化的分类器。
        Classifier classifier;
        ObjectInputStream ois = new ObjectInputStream (new FileInputStream (new File(savedPath)));
        classifier = (Classifier) ois.readObject();
        ois.close();
        return classifier;
    }
    
    //predict & evaluate
    public String predict(Classifier classifier,Instance testInstance){
        Labeling labeling = classifier.classify(testInstance).getLabeling();
        cc.mallet.types.Label label = labeling.getBestLabel();
        return (String)label.getEntry();
    }
    
    public void evaluate(Classifier classifier, String testFilePath) throws IOException {
        InstanceList testInstances = new InstanceList(classifier.getInstancePipe());                                                                                                                                                                
        //包含测试数据的实例列表
        //format of input data:[name] [label] [data ... ]     创建一个新的迭代器，读取原始实例数据                                                               
        CsvIterator reader = new CsvIterator(new FileReader(new File(testFilePath)),"(\\w+)\\s+(\\w+)\\s+(.*)",3, 2, 1);  // (data, label, name) field indices               

        // Add all instances loaded by the iterator to our instance list
        //  our instance list, passing the raw input data                                                  
        //  through the classifier's original input pipe.    
        testInstances.addThruPipe(reader);
        Trial trial = new Trial(classifier, testInstances);

        //evaluation metrics.precision, recall, and F1
//        System.out.println("Accuracy: " + trial.getAccuracy());                                                      
//        System.out.println("F1 for class 'good': " + trial.getF1("good"));
//        System.out.println("Precision for class '" +
//                           classifier.getLabelAlphabet().lookupLabel(1) + "': " +
//                           trial.getPrecision(1));
    }

    //perform n-fold cross validation
//     public static Trial testTrainSplit(MaxEntTrainer trainer, InstanceList instances) {
//         int TRAINING = 0;
//         int TESTING = 1;
//         int VALIDATION = 2;
//     
//         // Split the input list into training (90%) and testing (10%) lists.
//         InstanceList[] instanceLists = instances.split(new Randoms(), new double[] {0.9, 0.1, 0.0});
//         Classifier classifier = trainClassifier(instanceLists[TRAINING]);
//         return new Trial(classifier, instanceLists[TESTING]);
//      }
     
     Pipe pipe;

     public Mallet() {
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
         
         // Tokenize raw strings记号化原始字符串
         pipeList.add(new CharSequence2TokenSequence(tokenPattern));

         // Normalize all tokens to all lowercase规范化
         pipeList.add(new TokenSequenceLowercase());
         
         // Remove stopwords from a standard English stoplist.删除停用词
         //  options: [case sensitive] [mark deletions]
         pipeList.add(new TokenSequenceRemoveStopwords(false, false));

         // Rather than storing tokens as strings, convert 
         //  them to integers by looking them up in an alphabet.转为int
         pipeList.add(new TokenSequence2FeatureSequence());

         // Do the same thing for the "target" field: 
         //  convert a class label string to a Label object,
         //  which has an index in a Label alphabet.
         pipeList.add(new Target2Label());

         // Now convert the sequence of features to a sparse vector,
         //  mapping feature IDs to counts.特征序列转为向量
         pipeList.add(new FeatureSequence2FeatureVector());

         // Print out the features and the label
         pipeList.add(new PrintInputAndTarget());

         return new SerialPipes(pipeList);
     }

     public InstanceList readDirectory(File directory) {
         return readDirectories(new File[] {directory});
     }

     public InstanceList readDirectories(File[] directories) {
         
         // Construct a file iterator, starting with the 
         //  specified directories, and recursing through subdirectories.
         // The second argument specifies a FileFilter to use to select
         //  files within a directory.//第二个参数指定的FileFilter使用到一个目录中选择文件。
    	 //第三个参数是应用到文件名来产生一个类标签的模式。使用最后一个目录名的路径。
         // The third argument is a Pattern that is applied to the 
         //   filename to produce a class label. In this case, I've 
         //   asked it to use the last directory name in the path.
         FileIterator iterator =
             new FileIterator(directories,
                              new TxtFilter(),
                              FileIterator.LAST_DIRECTORY);
         
         // Construct a new instance list, passing it the pipe
         //  we want to use to process instances.
         InstanceList instances = new InstanceList(pipe);

         // Now process each instance provided by the iterator.
         instances.addThruPipe(iterator);

         return instances;
     }
     
     /** This class illustrates how to build a simple file filter */
     class TxtFilter implements FileFilter {
    	 //文件过滤器
         /** Test whether the string representation of the file 
          *   ends with the correct extension. Note that {@ref FileIterator}
          *   will only call this filter if the file is not a directory,
          *   so we do not need to test that it is a file.
          */
         public boolean accept(File file) {
             return file.toString().endsWith(".html");
         }
     }

	
	static public void Related () throws IOException{
		Mallet naivebays = new Mallet();
        InstanceList instances = naivebays.readDirectory(new File("./src/web/Sample1")); //训练集
        Classifier classifier = naivebays.trainClassifier(instances);
        InstanceList test = naivebays.readDirectory(new File("./src/web/pages"));//测试集
        for(int i=0;i<test.size();i++)
        {
        	Instance testinstance = test.get(i);
        	
        	String filepath=testinstance.getName().toString();
        	filepath=filepath.replace("file:/", "");
//        	System.out.println(filepath);
        	String[] name = filepath.split("/");
        	BufferedReader fr = new BufferedReader (new InputStreamReader (new FileInputStream (filepath), "UTF-8"));
            if(naivebays.predict(classifier, testinstance).equals("Related"))
            {
            	
            	BufferedWriter fw = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("./src/web/Classification/related/"+name[name.length-1]), "UTF-8"));
				int ch=0;
				while((ch=fr.read())!=-1)
				{
					fw.write(ch);
				}
				fw.close();
				fr.close();
               
            }
            else
            {
            	BufferedWriter fw1 = new BufferedWriter (new OutputStreamWriter (new FileOutputStream ("./src/web/Classification/disrelated/"+name[name.length-1]), "UTF-8"));
				int ch1=0;
				while((ch1=fr.read())!=-1)
				{
					fw1.write(ch1);
				}
				fw1.close();
				fr.close();            
			}
           System.out.println(testinstance.getName()+"\t result:"+naivebays.predict(classifier, testinstance));
        }
	}
}
