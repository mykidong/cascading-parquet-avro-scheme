package cascading.scheme;

import java.io.IOException;
import java.util.Collection;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroSerialization;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.InputFormat;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.OutputFormat;
import org.apache.hadoop.mapred.RecordReader;

import parquet.avro.AvroParquetInputFormat;
import parquet.avro.AvroParquetOutputFormat;
import parquet.avro.AvroReadSupport;
import cascading.avro.serialization.AvroSpecificRecordSerialization;
import cascading.flow.FlowProcess;
import cascading.tap.Tap;
import cascading.tuple.Tuple;

public class ParquetAvroScheme extends DeprecatedAvroScheme {
	
	public ParquetAvroScheme(Schema schema)
	{
		super(schema);
	}
	
	 @Override
	public boolean source(FlowProcess<? extends Configuration> flowProcess,
			SourceCall<Object[], RecordReader> sourceCall) throws IOException {

		RecordReader<Void, GenericRecord> input = sourceCall.getInput();
		
		GenericRecord genericRecord = input.createValue();
		
		if (!input.next(input.createKey(), genericRecord)) {
			return false;
		}		
		
		Record record = new Record(genericRecord.getSchema());
		
		for(Field field : genericRecord.getSchema().getFields())
		{
			record.put(field.name(), genericRecord.get(field.name()));
		}		
		
		Tuple tuple = sourceCall.getIncomingEntry().getTuple();
		tuple.clear();

		Object[] split = DeprecatedAvroToCascading.parseRecord(record, schema);
		tuple.addAll(split);

		return true;
	}
	
	@Override
	public void sourceConfInit(FlowProcess<? extends Configuration> flowProcess,
			Tap<Configuration, RecordReader, OutputCollector> tap, Configuration conf) {
		
		retrieveSourceFields(flowProcess, tap);
	
		// Set the input schema and input class
		conf.set(AvroReadSupport.AVRO_REQUESTED_PROJECTION, schema.toString());			
		conf.setClass("mapred.input.format.class", AvroParquetInputFormat.class, InputFormat.class);
		
	
		// add AvroSerialization to io.serializations
		addAvroSerializations(conf);			
	}

	@Override
	public void sinkConfInit(FlowProcess<? extends Configuration> flowProcess,
			Tap<Configuration, RecordReader, OutputCollector> tap, Configuration conf) {

		if (schema == null) {
			throw new RuntimeException("Must provide sink schema");
		}
		
		
		// Set the input schema and input class
		conf.set("parquet.avro.schema", schema.toString());		
		
		conf.setClass("mapred.output.format.class", AvroParquetOutputFormat.class, OutputFormat.class);
		
		
		// add AvroSerialization to io.serializations
		addAvroSerializations(conf);
		
	}
	

	private void addAvroSerializations(Configuration conf) {
		Collection<String> serializations = conf
				.getStringCollection("io.serializations");
		if (!serializations.contains(AvroSerialization.class.getName())) {
			serializations.add(AvroSerialization.class.getName());
			serializations.add(AvroSpecificRecordSerialization.class.getName());
		}

		conf.setStrings("io.serializations",
				serializations.toArray(new String[serializations.size()]));
	}

}
