# cascading-parquet-avro-scheme
This is Cascading Parquet Avro Scheme which can be used with avro schema.

This Cascading Scheme conforms to Cascading 3.0 API.


# Usage
For instance, in the classpath, Avro Schema File '/META-INF/avro/item-view-event.avsc' would look lik this:

 ```json
 {
   "type":"record",
   "name":"ItemViewEvent",   
   "fields":[
   	  {
         "name":"baseProperties",
         "type":{
           "type":"record",
           "name":"BaseProperties",         
           "fields":[             
              {"name":"serviceId", "type":"string"},           
              {"name":"uid", "type":["null", "string"]},
              {"name":"pcid", "type":"string"}, 
              {"name":"timestamp", "type":"long"}
           ]
         }         
      }, 
      {"name":"itemId", "type":"string"}    
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


# Maven

  ```xml
  <repository>
	<id>conjars.org</id>
	<url>http://conjars.org/repo</url>
  </repository>
  ```

  ```xml
  <dependency>
  	<groupId>cascading-scheme</groupId>
  	<artifactId>cascading-parquet-avro-scheme</artifactId>
  	<version>0.1.0-SNAPSHOT</version>
  </dependency>
  ```
