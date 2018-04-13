import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Partitioner;

// 自定义分区器
public class ItemIdPartitioner extends Partitioner<OrderBean, NullWritable>{

	@Override
	public int getPartition(OrderBean bean, NullWritable value, int numReduceTasks) {
		// Integer.MAX_VALUE 也就是 0x 7fff ffff，32位二进制数，最高位为0，其他为1
		/*
		 * 我的理解，如果你的 numDeduceTasks 设置为 16 的话，那么你就要与 0x xxxx xxxf 相与（最后 4 位必须全为），
		 * 否则有的分区会没有数据，有点类似于 hashmap 的思路。
		 *
		 * 相同的订单会被发往相同的分区，但注意不保证不同的订单发往不同的。因为 hashCode 和 equals 的关系是
		 * 如果对象相等，那么hashcode 必须相同，但是对象不相等，不强制要求 hahcode 不同，但推荐不同
		 */
		return(bean.getItemid().hashCode() & Integer.MAX_VALUE) % numReduceTasks;
	}
	
}
