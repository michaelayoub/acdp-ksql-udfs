# acdp-ksql-udfs

User-defined functions for ACDP (ACEmulator Data Platform).

## Static Data Source

The static data is sourced from Protobuf files which should be placed in
`src/main/resources`. When ksqlDB instantiates the UDFs, the appropriate
Protobuf file is imported into memory. The majority of the Protobuf files
are produced by code in [acdp-ace-extractors](https://github.com/michaelayoub/acdp-ace-extractors);
the POI data is produced by the code in [acdp-python](https://github.com/michaelayoub/acdp-python)
which just pulls the data from the ACEmulator MySQL database.

## Example usage

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

## Functions

### ace_enum

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

### ace_nearest_poi

```
Name        : ACE_NEAREST_POI
Author      : Michael Ayoub
Version     : 0.1
Overview    : A function for finding the nearest POIs to a position.
Type        : SCALAR
Jar         : /opt/ksqldb-udfs/acdp-ksql-udfs-0.0.1.jar
Variations  :

 Variation   : ACE_NEAREST_POI(landBlockId BIGINT, x DOUBLE, y DOUBLE, z DOUBLE, k INT)
 Returns     : ARRAY<VARCHAR>
 Description : Return the nearest k POIs to a position.

 Variation   : ACE_NEAREST_POI(landBlockId BIGINT, x DOUBLE, y DOUBLE, z DOUBLE)
 Returns     : VARCHAR
 Description : Return the nearest POI to a position.
```

### ace_skill

```
Name        : ACE_SKILL
Author      : Michael Ayoub
Version     : 0.1
Overview    : Return the attribute contribution to a skill. `attributes` should be an array of attribute values, and `attributeKeys` should be an
              array with corresponding entries of the attribute types; for example, collect_list(current_level) and collect_list(type),
              respectively.
Type        : SCALAR
Jar         : /opt/ksqldb-udfs/acdp-ksql-udfs-0.0.1.jar
Variations  :

 Variation   : ACE_SKILL(skillDetails STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED INT, XP_SPENT BIGINT, SKILL_ADVANCEMENT_CLASS_TYPE BIGINT, SKILL_ADVANCEMENT_CLASS_NAME VARCHAR, LAST_USED_TIMESTAMP VARCHAR>, attributeDetailList ARRAY<STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED BIGINT, XP_SPENT BIGINT, CURRENT_LEVEL BIGINT>>)
 Returns     : STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED INT, LEVEL_FROM_ATTRIBUTES INT, CURRENT_LEVEL INT, XP_SPENT BIGINT, SKILL_ADVANCEMENT_CLASS_TYPE BIGINT, SKILL_ADVANCEMENT_CLASS_NAME VARCHAR, LAST_USED_TIMESTAMP VARCHAR>
```

### ace_attribute_collect

```
Name        : ACE_ATTRIBUTE_COLLECT
Author      : Michael Ayoub
Version     : 0.1
Overview    : Collect rows for individual attributes and produce a single array of attributes sorted by type.
Type        : AGGREGATE
Jar         : /opt/ksqldb-udfs/acdp-ksql-udfs-0.0.1.jar
Variations  :

 Variation   : ACE_ATTRIBUTE_COLLECT(val1 STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED BIGINT, XP_SPENT BIGINT, CURRENT_LEVEL BIGINT>)
 Returns     : ARRAY<STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED BIGINT, XP_SPENT BIGINT, CURRENT_LEVEL BIGINT>>
 Description : Collect the latest attribute values
```

### ace_attribute2_collect

```
Name        : ACE_ATTRIBUTE2_COLLECT
Author      : Michael Ayoub
Version     : 0.1
Overview    : Collect rows for individual secondary attributes and produce a single array of secondary attributes sorted by type.
Type        : AGGREGATE
Jar         : /opt/ksqldb-udfs/acdp-ksql-udfs-0.0.1.jar
Variations  :

 Variation   : ACE_ATTRIBUTE2_COLLECT(val1 STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED BIGINT, XP_SPENT BIGINT, CURRENT_LEVEL BIGINT, CURRENT_LEVEL_WITH_ENCHANTMENTS BIGINT>)
 Returns     : ARRAY<STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED BIGINT, XP_SPENT BIGINT, CURRENT_LEVEL BIGINT, CURRENT_LEVEL_WITH_ENCHANTMENTS BIGINT>>
 Description : Collect the latest secondary attribute values
```

### ace_skill_collect

```
Name        : ACE_SKILL_COLLECT
Author      : Michael Ayoub
Version     : 0.1
Overview    : Collect rows for individual skills and produce a single array of skills sorted by type.
Type        : AGGREGATE
Jar         : /opt/ksqldb-udfs/acdp-ksql-udfs-0.0.1.jar
Variations  :

 Variation   : ACE_SKILL_COLLECT(val1 STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED INT, LEVEL_FROM_ATTRIBUTES INT, CURRENT_LEVEL INT, XP_SPENT BIGINT, SKILL_ADVANCEMENT_CLASS_TYPE BIGINT, SKILL_ADVANCEMENT_CLASS_NAME VARCHAR, LAST_USED_TIMESTAMP VARCHAR>)
 Returns     : ARRAY<STRUCT<PROPERTY_TYPE INT, PROPERTY_NAME VARCHAR, INITIAL_LEVEL BIGINT, NUM_TIMES_INCREASED INT, LEVEL_FROM_ATTRIBUTES INT, CURRENT_LEVEL INT, XP_SPENT BIGINT, SKILL_ADVANCEMENT_CLASS_TYPE BIGINT, SKILL_ADVANCEMENT_CLASS_NAME VARCHAR, LAST_USED_TIMESTAMP VARCHAR>>
 Description : Collect the latest skill values
```
