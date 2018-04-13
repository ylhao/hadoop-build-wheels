import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

// 注意这里实现的接口类型是 WritableComparable
/*

WirtableComparable 接口如下
public interface WritableComparable<T> extends Writable, Comparable<T> {
}

在默认情况下 Hadoop 是按照 Key 值的 CompareTo 方法进行排序和分组的，Hadoop 对常用的 Java 基本数据类型以及对象等都进行了包装，
将他们包装成 WritableCompatrable 对象，并且都实现了compareTo 方法。如果自定义数据类型作为 Key 的话，必须要实现 WritableComparable 接口。

总结：自定义对象作为 Key，必须实现 WritableComparable 接口，实现该接口中的 compareTo 方法，默认情况下按照 Key 对应的类别的 CompareTo 方法进行排序和分组

当然 Hadoop 还允许程序员自定义相应的排序对比器和分组排序对比器来对 Key 值进行灵活的排序和分组
 *
 */
public class OrderBean implements WritableComparable<OrderBean>{
	
	private Text itemid;  // 订单号
	private DoubleWritable amount; // 订单金额
	
	public OrderBean() {
		
	}
	
	public OrderBean(Text id, DoubleWritable amount) {
		this.itemid = id;
		this.amount = amount;
	}
	
	public Text getItemid() {
		return itemid;
	}

	public void setItemid(Text itemid) {
		this.itemid = itemid;
	}

	public DoubleWritable getAmount() {
		return amount;
	}

	public void setAmount(DoubleWritable amount) {
		this.amount = amount;
	}
	
	// 实现比较的方法
	public int compareTo(OrderBean o) {
		
		// 比较两个订单号是否相同
		// itemid 是 Text 类型的对象，调用其 compareTo 方法，传入一个 Text 类型的对象进行比较
		int ret = itemid.compareTo(o.getItemid());
		// 如果两个订单的订单号相等
		if(ret == 0) {
			// 比较金额
			/*
			 * 	DoubleWritable 的 compareTo 方法如下
			 * 当前对象的值 < 传入对象的值，返回 -1
				public int compareTo(DoubleWritable o) {
    		 		return (value < o.value ? -1 : (value == o.value ? 0 : 1));
  				}
			 */
			// 我们取个反，也就是说当 当前对象的值比传入对象的值小的时候，我们返回 1，这是为了让同一分区上的数据降序排序
			ret = -amount.compareTo(o.getAmount());
		}
		return ret;
	}
	
	// 实现 WritableComparable 的 readFileds 实现反序列化
	// 传入一个 DataInput 类型的对象
	// 注意与 write 方法对应起来
	public void readFields(DataInput in) throws IOException {
		this.itemid = new Text(in.readUTF());
		this.amount = new DoubleWritable(in.readDouble());
	}
	
	// 实现该方法实现序列化
	public void write(DataOutput out) throws IOException {
		out.writeUTF(itemid.toString());
		// amount 是 DoubleWritable 类型的对象，调用 get 方法，返回一个 double 类型的变量
		out.writeDouble(amount.get());
	}

	@Override
	public String toString() {
		return "OrderBean [itemid=" + itemid + ", amount=" + amount + "]";
	}
	
}
