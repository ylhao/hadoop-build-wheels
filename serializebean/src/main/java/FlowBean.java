import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class FlowBean implements Writable{
	
	private long upFlow;  // 上行流量
	private long dFlow;  // 下行流量
	private long sumFlow;  // 总流量
	
	// 提供无参构造函数
	public FlowBean() {
		
	}
	
	// 构造函数
	public FlowBean(long upFlow, long dFlow) {
		this.upFlow = upFlow;
		this.dFlow = dFlow;
		this.sumFlow = upFlow + dFlow;
	}
	
	public long getUpFlow() {
		return upFlow;
	}

	public void setUpFlow(long upFlow) {
		this.upFlow = upFlow;
	}

	public long getdFlow() {
		return dFlow;
	}

	public void setdFlow(long dFlow) {
		this.dFlow = dFlow;
	}

	public long getSumFlow() {
		return sumFlow;
	}

	public void setSumFlow(long sumFlow) {
		this.sumFlow = sumFlow;
	}

	// 反序列化
	public void readFields(DataInput in) throws IOException {
		upFlow = in.readLong();
		dFlow = in.readLong();
		sumFlow = in.readLong();
	}

	// 序列化
	public void write(DataOutput out) throws IOException {
		out.writeLong(upFlow);
		out.writeLong(dFlow);
		out.writeLong(sumFlow);
	}

	// 重写 toString 方法
	@Override
	public String toString() {
		return "FlowBean [upFlow=" + upFlow + ", dFlow=" + dFlow + ", sumFlow=" + sumFlow + "]";
	}
	
	
}
