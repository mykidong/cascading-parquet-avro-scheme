# cascading-parquet-avro-scheme
This is Cascading Parquet Avro Scheme which can be used with avro schema.


# Usage
For instance, in the classpath, Avro Schema File '/META-INF/avro/item-view-event.avsc' would look lik this:

 ```json
 {
    "type":"record",
   "name":"ItemViewEvent",
   "namespace":"avro.domain.event.schema",
   "fields":[
      {
         "name":"baseProperties",
         "type":[
            "null",
            {
               "type":"record",
               "name":"BaseProperties",
               "namespace":"avro.domain.event.schema",
               "fields":[
                  {
                     "name":"serviceId",
                     "type":[
                        "null",
                        "string"
                     ]
                  },
                  {
                     "name":"uid",
                     "type":[
                        "null",
                        "string"
                     ]
                  },
                  {
                     "name":"pcid",
                     "type":[
                        "null",
                        "string"
                     ]
                  },                  
                  {
                     "name":"timestamp",
                     "type":[
                        "null",
                        "long"
                     ]
                  }
               ]
            }
         ]
      },
      {
         "name":"itemId",
         "type":[
            "null",
            "string"
         ]
      }
   ]
 }
 ```



To instantiate ParquetAvroScheme:


    // ParquetAvroScheme instantiated with avro schema from the classpath. 
    ParquetAvroScheme itemViewEventParquetScheme = 
      new ParquetAvroScheme(new Schema.Parser().parse(getClass().getResourceAsStream("/META-INF/avro/item-view-event.avsc")));
		
		
	// if this scheme is used in the source tap, set source fields.
	itemViewEventParquetScheme.setSourceFields(new Fields("?base-properties", "?item-id"));	
		
	// if this scheme is used in the sink tap, set sink fields.
	itemViewEventParquetScheme.setSinkFields(new Fields("?base-properties", "?item-id"));	
		
		
	// source or sink tap.
	Tap tap = new Hfs(itemViewEventParquetScheme, "/parquet-path");	

