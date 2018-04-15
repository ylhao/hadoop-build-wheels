import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

public class MyInputFormat extends FileInputFormat<NullWritable, BytesWritable>{

	@Override
	protected boolean isSplitable(JobContext context, Path filename) {
		// 保证一个文件生成一个 key-value 键值对
		return false;
	}

	@Override
	public RecordReader<NullWritable, BytesWritable> createRecordReader(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		// 我们在 createRecorder 方法中创建一个自定义的 recordReader
		MyRecordReader recordReader = new MyRecordReader();
		recordReader.initialize(split, context);
		return recordReader;
	}

}
