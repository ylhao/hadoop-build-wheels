import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WordCount {
	 /** 
     * Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
     * KEYIN: 输入 KEY 类型
     * VALUEIN: 输入 VALUE 类型
     * KEYOUT: 输出 KEY 类型
     * VALUEOUT: 输出 VALUE 类型
     */
	/*
	 *  ToenizerMapper 继承了 Mapper 类，重写了 map 方法，MapReduce 框架每读一行数据，就调用一次 map 方法
	 *  map 方法接收一个 key、value 对儿，然后进行一系列处理，输出一个 key、value 对儿
	 *  综上：MapReduce 框架每读一行数据，就把这行数据包装成 key、value 的形式，key 默认情况下是该行文本的起始偏移量，
	 *  value 在默认情况下是改行的数据内容（String 类型），然后将 key、value 传给 map 方法，调用 map 方法处理该行，最后
	 *  返回一个 key、value 对儿
	 * */
	
	/*
	 * 类型的说明：
	 * mapreduce 程序的输出数据需要在不同的机器之间传输，所以必须是可序列化的，hadoop 中定义了自己的可序列化类型
	 * long -> LongWritable
	 * int -> IntWritable
	 * String -> Text
	 */
	public static class TokenizerMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
			// 得到每一行的数据
			String line = value.toString();
			// 分词：将每行的单词进行分割,按照"  \t\n\r\f"(空格、制表符、换行符、回车符、换页)进行分割
            StringTokenizer tokenizer = new StringTokenizer(line);
			// 通过空格分隔
			// String[] words = line.split(" ");
			// 循环遍历输出
			// for(String word : words) {
			//	context.write(new Text(word), new IntWritable(1));
			//}
            // 遍历，输出
            while(tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
                context.write(new Text(word), new IntWritable(1));
            }
		}
	}
		
	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		/*
		 *  注意 reduce 接收的一个字符串类型的 key，一个可迭代的数据集
		 */
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
			Integer count = 0;
			for(IntWritable value : values) {
				count += value.get();
			}
			context.write(key, new IntWritable(count));
		}
	}
	
	public static void main(String[] args) throws Exception {
		// 创建配置对象
		Configuration conf = new Configuration();
	    // 创建 Job 对象
		Job job = Job.getInstance(conf, "word count");
	    // 设置 Job 运行时的类
		job.setJarByClass(WordCount.class);
		// 设置 Mapper 类
	    job.setMapperClass(TokenizerMapper.class);
	    // 指定 Combiner 类
	    job.setCombinerClass(IntSumReducer.class);
	    // 设置 map 输出的 key
	    job.setMapOutputKeyClass(Text.class);
	    // 设置 map 输出的 value
	    job.setMapOutputValueClass(IntWritable.class);
	    // 设置 Reducer 类
	    job.setReducerClass(IntSumReducer.class);
	    // 设置 reduce 输出的 key
	    job.setOutputKeyClass(Text.class);
	    // 设置 reduce 输出的 value
	    job.setOutputValueClass(IntWritable.class);
	    // 设置输入输出的路径
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    // 提交 job，等待运行结果，并在客户端显示运行信息，最后结束程序
	    System.exit(job.waitForCompletion(true) ? 0 : 1); 
	}
}
