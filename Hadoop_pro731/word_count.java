#new package in eclipse
#map_reduce code import eclipse 扩展包（手动导入）
＃创建配置文件config，后面生成可执行java包时使用，执行在3个ubuntu上执行，master上执行hadoop命令，会自动分配任务
package org.apache.hadoop.examples;
 
import java.io.IOException;
import java.util.StringTokenizer;
 
importorg.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
importorg.apache.hadoop.mapreduce.lib.input.FileInputFormat;
importorg.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
importorg.apache.hadoop.util.GenericOptionsParser;
 
public class WordCount {
/**
MapReduceBase类:实现了Mapper和Reducer接口的基类（其中的方法只是实现接口，而未作任何事情）
Mapper接口：
WritableComparable接口：实现WritableComparable的类可以相互比较。所有被用作key的类应该实现此接口。
Reporter 则可用于报告整个应用的运行进度，本例中未使用。
LongWritable, IntWritable, Text 均是 Hadoop 中实现的用于封装 Java 数据类型的类，这些类实现了WritableComparable接口，都能够被串行化从而便于在分布式环境中进行数据交换，你可以将它们分别视为long,int,String 的替代品。
**/
 public static class TokenizerMapper
      extends Mapper<Object, Text, Text, IntWritable>{
   
   private final static IntWritable one = new IntWritable(1);
   private Text word = new Text();
/**
Mapper接口中的map方法，
Void map(K1key, V1 value, OutputCollector<K2,V2> output, Reporter reporter)
映射一个单个的输入k/v对到一个中间的k/v对
输出对不需要和输入对有相同的类型，输入对可以对应不同数量的输出对
OutputCollector接口：收集Mapper和Reducer输出的<k,v>对
OutputColletctor接口的collect(k,v)方法，增加一个（k/v）对到output
**/ 
   public void map(Object key, Text value, Context context
                    ) throws IOException,InterruptedException {
     StringTokenizer itr = new StringTokenizer(value.toString());
     while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
       context.write(word, one);
     }
    }
  }
 
 public static class IntSumReducer
      extends Reducer<Text,IntWritable,Text,IntWritable> {
   private IntWritable result = new IntWritable();
 
   public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException,InterruptedException {
     int sum = 0;
     for (IntWritable val : values) {
       sum += val.get();
      }
     result.set(sum);
     context.write(key, result);
    }
  }
 
  publicstatic void main(String[] args) throws Exception
{
/**
* JobConf：map/reduce的job配置类，向hadoop框架描述map-reduce执行的工作
* 构造方法：JobConf()、JobConf(ClassexampleClass)、JobConf(Configuration conf)等
*/
JobConf conf = new JobConf(WordCount.class);
conf.setJobName("wordcount"); //设置一个用户定义的job名称
 
conf.setOutputKeyClass(Text.class); //为job的输出数据设置Key类
conf.setOutputValueClass(IntWritable.class);//为job输出设置value类
 
conf.setMapperClass(Map.class); //为job设置Mapper类
conf.setCombinerClass(Reduce.class); //为job设置Combiner类
conf.setReducerClass(Reduce.class); //为job设置Reduce类
 
conf.setInputFormat(TextInputFormat.class);//为map-reduce任务设置InputFormat实现类
conf.setOutputFormat(TextOutputFormat.class);//为map-reduce任务设置OutputFormat实现类
 
/**
* InputFormat描述map-reduce中对job的输入定义
* setInputPaths():为map-reducejob设置路径数组作为输入列表
* setInputPath()：为map-reducejob设置路径数组作为输出列表
*/
FileInputFormat.setInputPaths(conf, newPath(args[0]));
FileOutputFormat.setOutputPath(conf, newPath(args[1]));
 
JobClient.runJob(conf); //运行一个job
}
}
