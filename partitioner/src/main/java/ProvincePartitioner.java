import java.util.HashMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class ProvincePartitioner extends Partitioner<Text, FlowBean> {

	// 类成员
	public static HashMap<String, Integer> proviceDict = new HashMap<>();
	
	// 静态代码块
	static {
		proviceDict.put("139", 3);  // 139 开头的手机号假定为 A 省，对应的分区为3
		proviceDict.put("137", 2);  // 137 开头的手机号假定为 B 省，对应的分区为2
		proviceDict.put("135", 1);  // 135 开头的手机号假定为 C 省，对应的分区为1
		proviceDict.put("133", 0);  // 133 开头的手机号假定为 D 省，对应的分区为0
		// 其他省份的我们不关心，归到第四个分区（见下面代码）
	}
	
	@Override
	public int getPartition(Text key, FlowBean value, int numPartitions) {
		// 先将 key 变为字符串，然后取字符串的前三个字符
		String prefix = key.toString().substring(0, 3);
		// get 方法，在不存在对应的键值的时候，返回 null
		Integer proviceId = proviceDict.get(prefix);
		// 如果不是我们关心的 4 个省份的，就归到第 4 个分区
		return proviceId == null ? 4 : proviceId;
	}

}
