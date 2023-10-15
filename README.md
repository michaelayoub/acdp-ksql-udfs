# acdp-ksql-udfs

User-defined functions for ACDP (ACEmulator Data Platform).

## ace_enum
```
Name        : ACE_ENUM
Author      : Michael Ayoub
Version     : 0.1
Overview    : A function for converting ACE Enum values to names.
Type        : SCALAR
Jar         : /opt/ksqldb-udfs/acdp-ksql-udfs-0.0.1.jar
Variations  :

	Variation   : ACE_ENUM(enumName VARCHAR, enumId BIGINT)
	Returns     : VARCHAR
	Description : Return the label for a given ACE Enumeration.

	Variation   : ACE_ENUM(enumName VARCHAR, enumId BIGINT, enumValue BIGINT)
	Returns     : VARCHAR
	Description : Return the extended value for a given ACE Enumeration value.
```

### Enumeration Source

The enumerations are sourced from a SQLite database file which is mounted
on the ksqlDB container along with this UDF. The database file is created
by a separate project (link forthcoming). The environment variable
`KSQL_KSQL_FUNCTIONS_ACEENUM.DB.PATH` points to this database file and is
used by the UDF during initialization.

### Example usage
Setup and input:
```sql
create stream s1 ( a int ) with ( kafka_topic = 's1', partitions = 1, value_format = 'avro' );

insert into s1 (a) values (1);
insert into s1 (a) values (125);
insert into s1 (a) values (207);

set 'auto.offset.reset' = 'earliest';

select a, ace_enum('PropertyInt', a) from s1 emit changes;
```
Output:
```text
+-----------------------------------------------------------------+-----------------------------------------------------------------+
|A                                                                |KSQL_COL_0                                                       |
+-----------------------------------------------------------------+-----------------------------------------------------------------+
|1                                                                |                                                                 |
|125                                                              |Age                                                              |
|207                                                              |CreatureKills                                                    |
```
