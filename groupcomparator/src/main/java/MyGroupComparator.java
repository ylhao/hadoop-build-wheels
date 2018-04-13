import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

// 自定义分组比较器
public class MyGroupComparator extends WritableComparator {
	public MyGroupComparator() {
		// 传入自定义的 OrderBean 类，OrderBean 中实现了我们自己的 CompareTo 方法，OrderBean 继承了 WritableComparable 接口
		super(OrderBean.class, true);
	}
	@Override
	// 我们定义的 OrderBean 实现了 WritableComparable 接口
	public int compare(WritableComparable a, WritableComparable b) {
		OrderBean ob1 = (OrderBean)a;
		OrderBean ob2 = (OrderBean)b;
		// 比较订单号是不是相同，订单号相同的放入一组，这里我们利用了一个 Hadoop 默认的行为，就是 Hadoop 默认选择第一条记录的 Key 作为这组记录的 Key
		
		// 我们的 OrderBean 中重写了 compareTo 方法，保证记录按订单号的默认规则进行排序并又按金额降序排序，所以每组记录的第一条恰好就是订单金额最大的
		return ob1.getItemid().compareTo(ob2.getItemid());
	}
}
