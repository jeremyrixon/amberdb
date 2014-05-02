package amberdb.sql.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

import amberdb.sql.AmberProperty;
import amberdb.sql.Lookups;
import amberdb.sql.PropertyMapper;


public interface AmberDao extends Transactional<AmberDao> {

    /*
     * DB creation operations (DDL)
     */
    
    /*
     * Main tables
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS vertex (" 
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, " 
            + "txn_end    BIGINT DEFAULT 0 NOT NULL)")
    void createVertexTable();
    
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS edge (" 
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, " 
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, "
            + "v_out      BIGINT, " 
            + "v_in       BIGINT, "
            + "label      VARCHAR(100), " 
            + "edge_order BIGINT)")
    void createEdgeTable();
    
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS property (" 
            + "id        BIGINT, "
            + "txn_start BIGINT DEFAULT 0 NOT NULL, " 
            + "txn_end   BIGINT DEFAULT 0 NOT NULL, "
            + "name      VARCHAR(100), " 
            + "type      CHAR(3), "
            + "value     BLOB)")
    void createPropertyTable();
    
    
    /*
     * Session tables
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_vertex ("
            + "s_id       BIGINT, " 
            + "id         BIGINT, "
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, " 
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, "
            + "state      CHAR(3))")
    void createSessionVertexTable();
    
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_edge (" 
            + "s_id       BIGINT, "
            + "id         BIGINT, " 
            + "txn_start  BIGINT DEFAULT 0 NOT NULL, "
            + "txn_end    BIGINT DEFAULT 0 NOT NULL, " 
            + "v_out      BIGINT, "
            + "v_in       BIGINT, " 
            + "label      VARCHAR(100), "
            + "edge_order BIGINT, " 
            + "state      CHAR(3))")
    void createSessionEdgeTable();
    
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS sess_property ("
            + "s_id      BIGINT, " 
            + "id        BIGINT, "
            + "name      VARCHAR(100), " 
            + "type      CHAR(3), "
            + "value     BLOB)")
    void createSessionPropertyTable();
    
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS id_generator ("
            + "id BIGINT PRIMARY KEY AUTO_INCREMENT)")
    void createIdGeneratorTable();
    
    
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS transaction ("
            + "id        BIGINT UNIQUE, " 
            + "time      BIGINT, "
            + "user      VARCHAR(100), " 
            + "operation TEXT)")
    void createTransactionTable();

    /*
     * Tools Lookup table - stores tools related reference data
     * 
     * TODOS: put indexes on the lookups table
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS lookups ("
            + "id        int(11), "
            + "name      varchar(50), "
            + "code      varchar(50), "
            + "attribute varchar(255), "
            + "value     varchar(4000), "
            + "deleted   varchar(1) default 'N' )")
    void createLookupTable();
    
    /*
     * Tools Many-To-Many association with multiple material types and software/device category
     * 
     * TODOS: put indexes on the maps table
     */
    @SqlUpdate(
            "CREATE TABLE IF NOT EXISTS maps("
            + "id        int(11), "
            + "parent_id int(11), "        
            + "deleted   varchar(1) default 'N' )")
    void createToolsMapsTable();   
    
    @SqlUpdate(
            "CREATE INDEX lookups_id_idx "
            + "ON lookups(id, deleted)")
    void createLookupsIdIndex();
    
    @SqlUpdate(
            "CREATE INDEX lookups_name_idx "
            + "ON lookups(name, deleted)")
    void createLookupsNameIndex();
    
    @SqlUpdate(
            "CREATE INDEX lookups_name_attribute_idx "
            + "ON lookups(name, attribute, deleted)")
    void createLookupsNameAttributeIndex();
    
    @SqlUpdate(
            "CREATE INDEX maps_id_idx "
            + "ON maps(id, deleted)")
    void createMapsIdIndex();
    
    @SqlUpdate(
            "CREATE INDEX maps_parent_id_idx "
            + "ON maps(parent_id, deleted)")
    void createMapsParentIdIndex();

    /*
     * General lookups
     */
    @SqlUpdate(
      "INSERT INTO lookups (name, value) VALUES"
      + "('copyType', 'Physical'),"
      + "('copyType', 'Digitised'),"
      + "('copyType', 'Born Digital'),"
      + "('copyRole', 'Related metadata'),"
      + "('copyRole', 'RealMedia reference'),"
      + "('copyRole', 'RealMedia file'),"
      + "('copyRole', 'QuickTime reference 1'),"
      + "('copyRole', 'QuickTime reference 2'),"
      + "('copyRole', 'QuickTime reference 3'),"
      + "('copyRole', 'QuickTime reference 4'),"
      + "('copyRole', 'QuickTime file 1'),"
      + "('copyRole', 'QuickTime file 2'),"
      + "('copyRole', 'Original'),"
      + "('copyRole', 'Thumbnail'),"
      + "('copyRole', 'View'),"
      + "('copyRole', 'Examination'),"
      + "('copyRole', 'Master'),"
      + "('copyRole', 'Co-master'),"
      + "('copyRole', 'Archive'),"
      + "('copyRole', 'Summary'),"
      + "('copyRole', 'Transcript'),"
      + "('copyRole', 'Structural map'),"
      + "('copyRole', 'Finding aid'),"
      + "('copyRole', 'Microform'),"
      + "('copyRole', 'Special delivery'),"
      + "('copyRole', 'Listen 1'),"
      + "('copyRole', 'Listen 2'),"
      + "('copyRole', 'Listen 3'),"
      + "('copyRole', 'Finding aid print'),"
      + "('copyRole', 'List'),"
      + "('copyRole', 'Derivative master'),"
      + "('copyRole', 'Analogue distribution'),"
      + "('copyRole', 'Working'),"
      + "('copyRole', 'Digital distribution'),"
      + "('copyRole', 'Time-coded Transcript'),"
      + "('copyRole', 'Print'),"
      + "('carrier', 'File system'),"
      + "('carrier', 'Working Reel'),"
      + "('carrier', 'User Cassette'),"
      + "('carrier', 'Online'),"
      + "('carrier', 'CD-R'),"
      + "('carrier', 'CD-ROM'),"
      + "('carrier', 'Second copy'),"
      + "('carrier', 'CD Archive'),"
      + "('carrier', 'CD Filtered'),"
      + "('carrier', 'Preservation Reel'),"
      + "('carrier', 'Safety DAT'),"
      + "('carrier', 'Cassette A'),"
      + "('carrier', 'Cassette B'),"
      + "('carrier', 'DAT'),"
      + "('carrier', 'Multitrack-ADAT'),"
      + "('carrier', 'Multitrack-DTRS'),"
      + "('carrier', 'Reel A'),"
      + "('carrier', 'Reel B'),"
      + "('carrier', 'CD Duplicate'),"
      + "('carrier', 'User CD'),"
      + "('carrier', 'Second Duplicate'),"
      + "('carrier', 'DVD Archive'),"
      + "('carrier', 'Safety Reel'),"
      + "('carrier', 'Microcassette'),"
      + "('carrier', 'NONE'),"
      + "('acquisitionStatus', 'CURRENT'),"
      + "('acquisitionStatus', 'BACKLOG'),"
      + "('acquisitionStatus', 'MS'),"
      + "('acquisitionCategory', 'Social History'),"
      + "('acquisitionCategory', 'Eminent Australians'),"
      + "('acquisitionCategory', 'Folklore'),"
      + "('acquisitionCategory', 'Others'),"
      + "('segmentIndicator', 'Non-timed'),"
      + "('segmentIndicator', 'Timed'),"
      + "('bitDepth', '8'),"
      + "('bitDepth', '12'),"
      + "('bitDepth', '14'),"
      + "('bitDepth', '16'),"
      + "('bitDepth', '18'),"
      + "('bitDepth', '20'),"
      + "('bitDepth', '22'),"
      + "('bitDepth', '24'),"
      + "('orientation', 'Portrait'),"
      + "('orientation', 'LandscapePortrait'),"
      + "('orientation', 'Landscape'),"
      + "('colourProfile', 'Within image'),"
      + "('colourProfile', 'External'),"
      + "('surface', 'Enhanced Azo'),"
      + "('surface', 'C-CrO2'),"
      + "('surface', 'C-Fe2O3'),"
      + "('surface', 'C-Metal'),"
      + "('surface', 'Cyanine'),"
      + "('surface', 'DOSS'),"
      + "('surface', 'Metal Azo'),"
      + "('surface', 'Pthalocyanine'),"
      + "('surface', 'Pthalocyanine Al T-Acetate'),"
      + "('surface', 'T-Paper'),"
      + "('surface', 'T-Polyester'),"
      + "('surface', 'T-PVC'),"
      + "('surface', 'Unsure'),"
      + "('carrierDuration', '2 min'),"
      + "('carrierDuration', '5 min'),"
      + "('carrierDuration', '12 min'),"
      + "('carrierDuration', '30 min'),"
      + "('carrierDuration', '40 min'),"
      + "('carrierDuration', '45 min'),"
      + "('carrierDuration', '46 min'),"
      + "('carrierDuration', '60 min'),"
      + "('carrierDuration', '63 min'),"
      + "('carrierDuration', '65 min'),"
      + "('carrierDuration', '74 min'),"
      + "('carrierDuration', '80 min'),"
      + "('carrierDuration', '90 min'),"
      + "('carrierDuration', '95 min'),"
      + "('carrierDuration', '96 min'),"
      + "('carrierDuration', '120 min'),"
      + "('carrierDuration', '122 min'),"
      + "('carrierDuration', '125 min'),"
      + "('carrierDuration', '180 min'),"
      + "('carrierDuration', '650 MB'),"
      + "('carrierDuration', '4.7 GB'),"
      + "('reelSize', '2in'),"
      + "('reelSize', '3in IEC'),"
      + "('reelSize', '4in IEC'),"
      + "('reelSize', '5in IEC'),"
      + "('reelSize', '5in NAB'),"
      + "('reelSize', '6in IEC'),"
      + "('reelSize', '7in IEC'),"
      + "('reelSize', '7in NAB'),"
      + "('reelSize', '8.25in IEC'),"
      + "('reelSize', '10in NAB'),"
      + "('reelSize', '10in IEC'),"
      + "('channel', '0.5'),"
      + "('channel', '1'),"
      + "('channel', '2'),"
      + "('channel', '4'),"
      + "('channel', '8'),"
      + "('channel', '16'),"
      + "('channel', '24'),"
      + "('speed', '2.38 cm/s'),"
      + "('speed', '4.76 cm/s'),"
      + "('speed', '9.5 cm/s'),"
      + "('speed', '19.05 cm/s'),"
      + "('speed', '38.1 cm/s'),"
      + "('speed', '76.2 cm/s'),"
      + "('thickness', 'Double play'),"
      + "('thickness', 'Long play'),"
      + "('thickness', 'Standard'),"
      + "('thickness', 'Triple play'),"
      + "('brand', 'EMITAPE 77/6'),"
      + "('brand', 'TDK 150'),"
      + "('brand', 'Scotch 131'),"
      + "('brand', 'AGFA PE 46'),"
      + "('brand', 'ACV'),"
      + "('brand', 'admark'),"
      + "('brand', '3M 807'),"
      + "('brand', 'ACME'),"
      + "('brand', 'AGFA'),"
      + "('brand', 'AGFA CARAT FeCr'),"
      + "('brand', 'AGFA DA DAT'),"
      + "('brand', 'AGFA DAT'),"
      + "('brand', 'AGFA FeI-S'),"
      + "('brand', 'AGFA FERRO COLOR'),"
      + "('brand', 'AGFA FERROCOLOR'),"
      + "('brand', 'AGFA FS'),"
      + "('brand', 'AGFA FS 396'),"
      + "('brand', 'AGFA GEVAERT'),"
      + "('brand', 'AGFA -GEVAERT PE36'),"
      + "('brand', 'AGFA Magnetonband'),"
      + "('brand', 'AGFA Magnetonband PER 525'),"
      + "('brand', 'AGFA PE 31'),"
      + "('brand', 'AGFA PE 36'),"
      + "('brand', 'AGFA PE 38'),"
      + "('brand', 'AGFA PE 4'),"
      + "('brand', 'AGFA PE 41'),"
      + "('brand', 'AGFA PE 65'),"
      + "('brand', 'AGFA PEM 369'),"
      + "('brand', 'AGFA PEM 468'),"
      + "('brand', 'AGFA PEM 469'),"
      + "('brand', 'AGFA PER'),"
      + "('brand', 'AGFA PER 368'),"
      + "('brand', 'AGFA PER 468'),"
      + "('brand', 'AGFA PER 525'),"
      + "('brand', 'AGFA PER 528'),"
      + "('brand', 'AGFA PER 555'),"
      + "('brand', 'AGFA SM'),"
      + "('brand', 'AGFA SUPER C60+6'),"
      + "('brand', 'AGFA SUPER COLOR'),"
      + "('brand', 'AGFA SUPERCHROM'),"
      + "('brand', 'AGFA WOLFEN'),"
      + "('brand', 'AGFA-GEVAERT'),"
      + "('brand', 'AGFA-GEVAERT 65'),"
      + "('brand', 'AGFA-GEVAERT SM'),"
      + "('brand', 'AIWA'),"
      + "('brand', 'AIWA DAT'),"
      + "('brand', 'AKAI'),"
      + "('brand', 'AKAI AT-5S'),"
      + "('brand', 'AKAI Ferro Extra I'),"
      + "('brand', 'AKAI HX'),"
      + "('brand', 'Allegro'),"
      + "('brand', 'ALMAG'),"
      + "('brand', 'ALPHA'),"
      + "('brand', 'AMCOLOR'),"
      + "('brand', 'AMERICAN'),"
      + "('brand', 'American Recording tape'),"
      + "('brand', 'AMPEX'),"
      + "('brand', 'AMPEX 1800'),"
      + "('brand', 'AMPEX 2020'),"
      + "('brand', 'AMPEX 2500'),"
      + "('brand', 'AMPEX 350'),"
      + "('brand', 'AMPEX 370'),"
      + "('brand', 'AMPEX 406'),"
      + "('brand', 'AMPEX 407'),"
      + "('brand', 'AMPEX 431'),"
      + "('brand', 'AMPEX 444'),"
      + "('brand', 'AMPEX 456'),"
      + "('brand', 'AMPEX 457'),"
      + "('brand', 'AMPEX 467 DAT'),"
      + "('brand', 'AMPEX 478'),"
      + "('brand', 'AMPEX 511'),"
      + "('brand', 'AMPEX 521'),"
      + "('brand', 'AMPEX 531'),"
      + "('brand', 'AMPEX 600'),"
      + "('brand', 'Ampex 611'),"
      + "('brand', 'AMPEX 631'),"
      + "('brand', 'AMPEX 632'),"
      + "('brand', 'AMPEX 641'),"
      + "('brand', 'AMPEX 642'),"
      + "('brand', 'AMPEX 679'),"
      + "('brand', 'AMPEX DAT'),"
      + "('brand', 'AMPEX EDR'),"
      + "('brand', 'AMPEX ELN'),"
      + "('brand', 'AMPEX GM'),"
      + "('brand', 'AMPEX GM3600'),"
      + "('brand', 'AMPEX GMI'),"
      + "('brand', 'AMPEX PLUS'),"
      + "('brand', 'AMPEX UDR'),"
      + "('brand', 'Anorgana Geno 1on Typ'),"
      + "('brand', 'Aristocrat'),"
      + "('brand', 'ARMSTRONG AUDIO'),"
      + "('brand', 'Astropulse'),"
      + "('brand', 'AUDIO DIMENSION'),"
      + "('brand', 'Audio Magnetics'),"
      + "('brand', 'AUDIO MAGNETICS CORPORATION'),"
      + "('brand', 'AUDIOMASTERS CORPORATION'),"
      + "('brand', 'AUDIOSONIC'),"
      + "('brand', 'audiotape'),"
      + "('brand', 'audiotape Q19'),"
      + "('brand', 'AUSTRALIAN HANIMEX'),"
      + "('brand', 'AVC'),"
      + "('brand', 'Award'),"
      + "('brand', 'BASF'),"
      + "('brand', 'BASF Chromdioxid'),"
      + "('brand', 'BASF chromdioxid super'),"
      + "('brand', 'BASF Chromdioxid Super II'),"
      + "('brand', 'BASF Chrome Extra II'),"
      + "('brand', 'BASF Chrome MaximaII'),"
      + "('brand', 'BASF CHROME SUPER II'),"
      + "('brand', 'BASF CR-M II'),"
      + "('brand', 'BASF DAT'),"
      + "('brand', 'BASF DP'),"
      + "('brand', 'BASF DP 26'),"
      + "('brand', 'BASF DP 26 LH'),"
      + "('brand', 'BASF DP26'),"
      + "('brand', 'BASF Ferro Extra I'),"
      + "('brand', 'BASF ferro maxima I'),"
      + "('brand', 'BASF Ferro Super I'),"
      + "('brand', 'BASF Ferro Super LH I'),"
      + "('brand', 'BASF ferro super LHI'),"
      + "('brand', 'BASF hifi'),"
      + "('brand', 'BASF LANGSPIELBAND'),"
      + "('brand', 'BASF LGH'),"
      + "('brand', 'BASF LGH 30 P'),"
      + "('brand', 'BASF LGR'),"
      + "('brand', 'BASF LGR 30 P'),"
      + "('brand', 'BASF LGR 35'),"
      + "('brand', 'BASF LGR P'),"
      + "('brand', 'BASF LGR P 30'),"
      + "('brand', 'BASF LGS'),"
      + "('brand', 'BASF LGS 26'),"
      + "('brand', 'BASF LGS 35'),"
      + "('brand', 'BASF LGS 52'),"
      + "('brand', 'BASF LH'),"
      + "('brand', 'BASF LH 90'),"
      + "('brand', 'BASF LH extra I'),"
      + "('brand', 'BASF LH SM'),"
      + "('brand', 'BASF LH Super'),"
      + "('brand', 'BASF LH SUPER 1'),"
      + "('brand', 'BASF LH Super I'),"
      + "('brand', 'BASF LH-EI'),"
      + "('brand', 'BASF LH-MI'),"
      + "('brand', 'BASF LHSM'),"
      + "('brand', 'BASF LN'),"
      + "('brand', 'BASF LN super 1'),"
      + "('brand', 'BASF LN super I'),"
      + "('brand', 'BASF LP'),"
      + "('brand', 'BASF LP35'),"
      + "('brand', 'BASF LPR 35'),"
      + "('brand', 'BASF LR 56'),"
      + "('brand', 'BASF PEM 369'),"
      + "('brand', 'BASF PEM 468'),"
      + "('brand', 'BASF PER 368'),"
      + "('brand', 'BASF PES 18'),"
      + "('brand', 'BASF SM'),"
      + "('brand', 'BASF SM 468'),"
      + "('brand', 'BASF SM 911'),"
      + "('brand', 'BASF SP 52'),"
      + "('brand', 'BASF SP50 LH'),"
      + "('brand', 'BASF SP54 R'),"
      + "('brand', 'BASF SP55 R'),"
      + "('brand', 'BASF SPR 50'),"
      + "('brand', 'BASF SPR 50 LH'),"
      + "('brand', 'BASF SPR 50 LHL'),"
      + "('brand', 'BASF TP 18 LH'),"
      + "('brand', 'BASF TYP LGN'),"
      + "('brand', 'BASF TYP LGS'),"
      + "('brand', 'BASF Typ LGS 26'),"
      + "('brand', 'BASF TYP LGS 52'),"
      + "('brand', 'BEL CLEER'),"
      + "('brand', 'BITCO'),"
      + "('brand', 'Brand 5'),"
      + "('brand', 'Brand Five'),"
      + "('brand', 'CAPITOL'),"
      + "('brand', 'certron'),"
      + "('brand', 'certron HE90'),"
      + "('brand', 'CFS Westinghouse-SPRcolor'),"
      + "('brand', 'Chapple'),"
      + "('brand', 'Compact Cassette'),"
      + "('brand', 'CONCERTAPE'),"
      + "('brand', 'Concorde'),"
      + "('brand', 'CONTRAST'),"
      + "('brand', 'CORONET HE'),"
      + "('brand', 'CORONET LN'),"
      + "('brand', 'Craig'),"
      + "('brand', 'CREST INTERNATIONAL'),"
      + "('brand', 'Crystal'),"
      + "('brand', 'cts'),"
      + "('brand', 'DENON'),"
      + "('brand', 'DENON DX'),"
      + "('brand', 'DENON DX1'),"
      + "('brand', 'DENON DX8'),"
      + "('brand', 'DENON LX'),"
      + "('brand', 'DEUTSCHE WELLE'),"
      + "('brand', 'Dick Smith'),"
      + "('brand', 'DICK SMITH EDR'),"
      + "('brand', 'Dindy'),"
      + "('brand', 'Dindy Black'),"
      + "('brand', 'Dindy Super'),"
      + "('brand', 'Dokorder'),"
      + "('brand', 'Dominion'),"
      + "('brand', 'Douglas Hi-Fi'),"
      + "('brand', 'duCros'),"
      + "('brand', 'EASTMAN KODAK'),"
      + "('brand', 'E-LITE'),"
      + "('brand', 'EMI'),"
      + "('brand', 'EMI Hi dynamic'),"
      + "('brand', 'EMITAPE'),"
      + "('brand', 'EMITAPE 100'),"
      + "('brand', 'EMITAPE 4'),"
      + "('brand', 'EMITAPE 5'),"
      + "('brand', 'EMITAPE 88'),"
      + "('brand', 'EMITAPE 88/12EH'),"
      + "('brand', 'EMITAPE 88/6'),"
      + "('brand', 'EMITAPE Afonic'),"
      + "('brand', 'EMITAPE HI-DYNAMIC'),"
      + "('brand', 'EMITAPE HI-DYNAMIC 9'),"
      + "('brand', 'EMITAPE HI-DYNAMIC HLP-9'),"
      + "('brand', 'EMITAPE HI-DYNAMIC HSP6'),"
      + "('brand', 'EMITAPE MAGNETIC'),"
      + "('brand', 'EMITAPE X1000'),"
      + "('brand', 'ferro MASTER UD'),"
      + "('brand', 'Ferrodynamics'),"
      + "('brand', 'Ferrograph-Ampex'),"
      + "('brand', 'FORWARD'),"
      + "('brand', 'FUJI'),"
      + "('brand', 'FUJI DAT'),"
      + "('brand', 'FUJI DR'),"
      + "('brand', 'FUJI DR-I'),"
      + "('brand', 'FUJI FILM'),"
      + "('brand', 'FUJI FILM FL'),"
      + "('brand', 'FUJI FILM FX'),"
      + "('brand', 'FUJI FL'),"
      + "('brand', 'FUJI FR-I'),"
      + "('brand', 'FUJI FR-I Super'),"
      + "('brand', 'FUJI FX-II'),"
      + "('brand', 'FUJI R DAT'),"
      + "('brand', 'Geloso'),"
      + "('brand', 'Gevaert'),"
      + "('brand', 'glatigny'),"
      + "('brand', 'GOLDRING'),"
      + "('brand', 'GOLDRING STUDIO RANGE'),"
      + "('brand', 'Graceline'),"
      + "('brand', 'GREENCORP'),"
      + "('brand', 'GRUNDIG'),"
      + "('brand', 'Grundig TLP2'),"
      + "('brand', 'GTape'),"
      + "('brand', 'GTape 900'),"
      + "('brand', 'GTape High Density'),"
      + "('brand', 'HANIMEX'),"
      + "('brand', 'HCL'),"
      + "('brand', 'HHB'),"
      + "('brand', 'HHb DA113DC'),"
      + "('brand', 'HITACHI'),"
      + "('brand', 'HITACHI DL'),"
      + "('brand', 'HITACHI Lo-D'),"
      + "('brand', 'HITACHI UD'),"
      + "('brand', 'HITACHI ULTRA'),"
      + "('brand', 'HITACHI ULTRA DYNAMIC'),"
      + "('brand', 'HI-TECH'),"
      + "('brand', 'ILFORD'),"
      + "('brand', 'imation'),"
      + "('brand', 'imitation'),"
      + "('brand', 'INTERNATIONAL'),"
      + "('brand', 'IRISH'),"
      + "('brand', 'IRU'),"
      + "('brand', 'KDK'),"
      + "('brand', 'KGC'),"
      + "('brand', 'KLARION'),"
      + "('brand', 'KODAK'),"
      + "('brand', 'KODAVOX'),"
      + "('brand', 'LAFAYETTE'),"
      + "('brand', 'Lancia'),"
      + "('brand', 'Lectra'),"
      + "('brand', 'LOEWE OPTA'),"
      + "('brand', 'M R SOUNDS'),"
      + "('brand', 'magna MAX2'),"
      + "('brand', 'MAGNASOUND'),"
      + "('brand', 'MALLORY'),"
      + "('brand', 'MARSHAL GOLDEN STUDIO'),"
      + "('brand', 'Marshall'),"
      + "('brand', 'MASTERTAPE'),"
      + "('brand', 'MASTERTONE'),"
      + "('brand', 'MAXELL'),"
      + "('brand', 'Maxell 35-90B'),"
      + "('brand', 'MAXELL A35.5'),"
      + "('brand', 'MAXELL A35-7'),"
      + "('brand', 'Maxell A50.5'),"
      + "('brand', 'maxell E25-7'),"
      + "('brand', 'Maxell LN'),"
      + "('brand', 'maxell MX'),"
      + "('brand', 'maxell S-LN'),"
      + "('brand', 'maxell UD'),"
      + "('brand', 'maxell UDI'),"
      + "('brand', 'maxell UDS-II'),"
      + "('brand', 'maxell UL'),"
      + "('brand', 'maxell UR'),"
      + "('brand', 'maxell XL II'),"
      + "('brand', 'maxell XLI'),"
      + "('brand', 'maxell XLI-S'),"
      + "('brand', 'MAX-WELL'),"
      + "('brand', 'Maxwell MX'),"
      + "('brand', 'Melody'),"
      + "('brand', 'MEMOREX'),"
      + "('brand', 'MEMOREX 1800'),"
      + "('brand', 'MEMOREX MRX 2'),"
      + "('brand', 'MEMOREX MRX I'),"
      + "('brand', 'mfp'),"
      + "('brand', 'MIDAS'),"
      + "('brand', 'MITAPE'),"
      + "('brand', 'MUSICWAY AUDIO'),"
      + "('brand', 'NAGU'),"
      + "('brand', 'NATIONAL'),"
      + "('brand', 'National EN'),"
      + "('brand', 'NATIONAL PANASONIC'),"
      + "('brand', 'NATIONAL PANASONIC RT'),"
      + "('brand', 'NATIONAL RT'),"
      + "('brand', 'NATIONAL RT 3'),"
      + "('brand', 'NATIONAL RT 3G'),"
      + "('brand', 'NATIONAL RT 5'),"
      + "('brand', 'NATIONAL RT-5G'),"
      + "('brand', 'NATIONAL RT-7G'),"
      + "('brand', 'No Frills'),"
      + "('brand', 'NORD'),"
      + "('brand', 'NORD BC'),"
      + "('brand', 'NORDIC'),"
      + "('brand', 'OCL'),"
      + "('brand', 'Olympic Technology'),"
      + "('brand', 'OPUS'),"
      + "('brand', 'OPUS UD'),"
      + "('brand', 'OPUS XD1'),"
      + "('brand', 'Pacific'),"
      + "('brand', 'PARAMOUNT'),"
      + "('brand', 'PDQ'),"
      + "('brand', 'PENNCREST'),"
      + "('brand', 'PERMATION'),"
      + "('brand', 'PERMATON'),"
      + "('brand', 'PHILIPS'),"
      + "('brand', 'PHILIPS AV'),"
      + "('brand', 'PHILIPS DP 13'),"
      + "('brand', 'PHILIPS DP 18'),"
      + "('brand', 'PHILIPS DP13'),"
      + "('brand', 'PHILIPS EL 3915'),"
      + "('brand', 'PHILIPS EL3914'),"
      + "('brand', 'PHILIPS EL3915'),"
      + "('brand', 'PHILIPS ER 13'),"
      + "('brand', 'PHILIPS ER 18'),"
      + "('brand', 'PHILIPS FE'),"
      + "('brand', 'PHILIPS FE I'),"
      + "('brand', 'PHILIPS FS'),"
      + "('brand', 'PHILIPS LP 10'),"
      + "('brand', 'PHILIPS LP 13'),"
      + "('brand', 'PHILIPS LP 15'),"
      + "('brand', 'PHILIPS LP 18'),"
      + "('brand', 'PRINZ'),"
      + "('brand', 'PRO DISC'),"
      + "('brand', 'PULSAR'),"
      + "('brand', 'PURVISONIC'),"
      + "('brand', 'Philips SP18'),"
      + "('brand', 'Pinnacle'),"
      + "('brand', 'PYRAL'),"
      + "('brand', 'PYROX'),"
      + "('brand', 'PYROX MAGICTAPE'),"
      + "('brand', 'QANTEM'),"
      + "('brand', 'QUANTEGY'),"
      + "('brand', 'RADIANT'),"
      + "('brand', 'Radio Shack'),"
      + "('brand', 'rainbow'),"
      + "('brand', 'Ralec'),"
      + "('brand', 'RALMAR'),"
      + "('brand', 'RALMER'),"
      + "('brand', 'RCA'),"
      + "('brand', 'RCA RED SEAL'),"
      + "('brand', 'REALISTIC'),"
      + "('brand', 'REALISTIC GOLD'),"
      + "('brand', 'REALISTIC LN'),"
      + "('brand', 'REALISTIC SUPERTAPE GOLD'),"
      + "('brand', 'recoton'),"
      + "('brand', 'Red Seal'),"
      + "('brand', 'ROBINS'),"
      + "('brand', 'ROLA'),"
      + "('brand', 'ROLATAPE'),"
      + "('brand', 'ROSS'),"
      + "('brand', 'ROYAL APC'),"
      + "('brand', 'RTC Playrite'),"
      + "('brand', 'RZ'),"
      + "('brand', 'SAISHO'),"
      + "('brand', 'SANYO'),"
      + "('brand', 'SAST'),"
      + "('brand', 'Scotch'),"
      + "('brand', 'Scotch 102'),"
      + "('brand', 'Scotch 111'),"
      + "('brand', 'Scotch 111A'),"
      + "('brand', 'Scotch 150'),"
      + "('brand', 'Scotch 175'),"
      + "('brand', 'Scotch 176'),"
      + "('brand', 'Scotch 177'),"
      + "('brand', 'Scotch 190'),"
      + "('brand', 'Scotch 200'),"
      + "('brand', 'Scotch 202'),"
      + "('brand', 'Scotch 203'),"
      + "('brand', 'Scotch 206'),"
      + "('brand', 'Scotch 207'),"
      + "('brand', 'Scotch 208'),"
      + "('brand', 'Scotch 215'),"
      + "('brand', 'Scotch 250'),"
      + "('brand', 'Scotch 9008'),"
      + "('brand', 'Scotch AV 177'),"
      + "('brand', 'Scotch Boy'),"
      + "('brand', 'Scotch BX'),"
      + "('brand', 'Scotch CLASSIC'),"
      + "('brand', 'Scotch DYNARANGE'),"
      + "('brand', 'Scotch Master'),"
      + "('brand', 'Scotch RB-5'),"
      + "('brand', 'Shamrock'),"
      + "('brand', 'SHARP'),"
      + "('brand', 'SILVER SONIC'),"
      + "('brand', 'silver sound'),"
      + "('brand', 'Silver TRAK UDX'),"
      + "('brand', 'Soni-Tape'),"
      + "('brand', 'SONOCOLOR'),"
      + "('brand', 'SONY'),"
      + "('brand', 'SONY 100'),"
      + "('brand', 'SONY 60'),"
      + "('brand', 'SONY AHF'),"
      + "('brand', 'SONY BHF'),"
      + "('brand', 'SONY C'),"
      + "('brand', 'SONY CHF'),"
      + "('brand', 'SONY DAT'),"
      + "('brand', 'SONY DC'),"
      + "('brand', 'SONY DT DAT'),"
      + "('brand', 'SONY EF'),"
      + "('brand', 'SONY HF'),"
      + "('brand', 'SONY PDP-65'),"
      + "('brand', 'SONY PDP-65C'),"
      + "('brand', 'SONY PR-150'),"
      + "('brand', 'SONY SUPER 150'),"
      + "('brand', 'SONY SUPER 300'),"
      + "('brand', 'SONY UCX'),"
      + "('brand', 'SONY UCX-S'),"
      + "('brand', 'SONY ULH'),"
      + "('brand', 'SONY UX'),"
      + "('brand', 'SONY UX-S'),"
      + "('brand', 'SONY ZX'),"
      + "('brand', 'SOUNDCRAFT'),"
      + "('brand', 'Star'),"
      + "('brand', 'STUDER'),"
      + "('brand', 'SUMMIT'),"
      + "('brand', 'Sunhing'),"
      + "('brand', 'SUPERTAPE GOLD'),"
      + "('brand', 'SUPERTAPE HD'),"
      + "('brand', 'SWEDA'),"
      + "('brand', 'T.D.J'),"
      + "('brand', 'TAMMY'),"
      + "('brand', 'tandberg radio'),"
      + "('brand', 'TARGET'),"
      + "('brand', 'TDK'),"
      + "('brand', 'TDK 90'),"
      + "('brand', 'TDK AD'),"
      + "('brand', 'TDK ADX'),"
      + "('brand', 'TDK AR'),"
      + "('brand', 'TDK ARX'),"
      + "('brand', 'TDK C'),"
      + "('brand', 'TDK D'),"
      + "('brand', 'TDK DA-R DAT'),"
      + "('brand', 'TDK DA-RXG'),"
      + "('brand', 'TDK F'),"
      + "('brand', 'TDK LN'),"
      + "('brand', 'TDK LX 35'),"
      + "('brand', 'TDK MA-R'),"
      + "('brand', 'TDK MA-X'),"
      + "('brand', 'TDK OD'),"
      + "('brand', 'TDK RC2'),"
      + "('brand', 'TDK SA'),"
      + "('brand', 'TDK SA-X'),"
      + "('brand', 'TDK SD'),"
      + "('brand', 'TDK SF'),"
      + "('brand', 'TDK Super Dynamic 1200 - SD'),"
      + "('brand', 'That''s'),"
      + "('brand', 'TDK Super Dynamic 1200-SD'),"
      + "('brand', 'TDK-E Synchroreel'),"
      + "('brand', 'TEAC'),"
      + "('brand', 'TEAC SOUND 52'),"
      + "('brand', 'TELEX'),"
      + "('brand', 'TEMPEST'),"
      + "('brand', 'That''s'),"
      + "('brand', 'thunder'),"
      + "('brand', 'TIANTAN'),"
      + "('brand', 'TONEX'),"
      + "('brand', 'TONEX Pastels'),"
      + "('brand', 'Toshiba'),"
      + "('brand', 'Tower'),"
      + "('brand', 'TRANSONIC'),"
      + "('brand', 'tronictape'),"
      + "('brand', 'TSL D'),"
      + "('brand', 'Ultra Sound'),"
      + "('brand', 'Unic'),"
      + "('brand', 'Unknown'),"
      + "('brand', 'VERBATIM'),"
      + "('brand', 'VIbrant'),"
      + "('brand', 'VIVA'),"
      + "('brand', 'W & G'),"
      + "('brand', 'WHSMITH'),"
      + "('brand', 'WORLD SLN'),"
      + "('brand', 'YASHIMA UFOI'),"
      + "('brand', 'ZENITH'),"
      + "('brand', 'EMITAPE 99'),"
      + "('brand', 'TEAC CDX'),"
      + "('brand', 'Dictasette'),"
      + "('brand', 'Zonal'),"
      + "('brand', 'TDK GX 35-90B'),"
      + "('brand', 'AMPEX 541'),"
      + "('brand', 'AMPEX 434'),"
      + "('brand', 'ORWO CR 35'),"
      + "('brand', 'Scotch RB-7'),"
      + "('brand', 'Scotch 220'),"
      + "('brand', 'PHILLIPS CD'),"
      + "('brand', 'ORWO'),"
      + "('brand', 'EMI Super'),"
      + "('brand', 'esc'),"
      + "('brand', 'BASF Ferro POWER I'),"
      + "('brand', 'TDK LX 50-60B'),"
      + "('brand', 'JVC MC-60SF'),"
      + "('durationType', 'Default'),"
      + "('durationType', 'Input'),"
      + "('equalisation', 'IEC'),"
      + "('equalisation', 'NAB'),"
      + "('samplingRate', '11000'),"
      + "('samplingRate', '22050'),"
      + "('samplingRate', '24000'),"
      + "('samplingRate', '32000'),"
      + "('samplingRate', '44100'),"
      + "('samplingRate', '48000'),"
      + "('samplingRate', '88200'),"
      + "('samplingRate', '96000'),"
      + "('samplingRate', '176400'),"
      + "('samplingRate', '192000'),"
      + "('soundField', 'Stereo'),"
      + "('soundField', 'Mono'),"
      + "('collection','nla.aus'),"
      + "('collection','nla.ms'),"
      + "('collection','nla.map'),"
      + "('collection','nla.mus'),"
      + "('collection','nla.pic'),"
      + "('collection','nla.gen'),"
      + "('collection','nla.oh'),"
      + "('collection','nla.int'),"
      + "('collection','nla.con'),"
      + "('subUnitType','Additional Material'),"
      + "('subUnitType','Attachment'),"
      + "('subUnitType','Bibliography'),"
      + "('subUnitType','Biography'),"
      + "('subUnitType','Board leaf'),"
      + "('subUnitType','Book'),"
      + "('subUnitType','Booklet'),"
      + "('subUnitType','Box'),"
      + "('subUnitType','Chapter'),"
      + "('subUnitType','Clipping'),"
      + "('subUnitType','Cover'),"
      + "('subUnitType','Cover - Back'),"
      + "('subUnitType','Cover - Front'),"
      + "('subUnitType','Cover - Inside Front '),"
      + "('subUnitType','Cover - Variant'),"
      + "('subUnitType','Cover - Inside Back '),"
      + "('subUnitType','Dedication'),"
      + "('subUnitType','Discography'),"
      + "('subUnitType','Enclosure'),"
      + "('subUnitType','End Papers'),"
      + "('subUnitType','Envelope'),"
      + "('subUnitType','Fly Leaf'),"
      + "('subUnitType','Folder'),"
      + "('subUnitType','Folio'),"
      + "('subUnitType','Foredge'),"
      + "('subUnitType','Game board'),"
      + "('subUnitType','Game instructions'),"
      + "('subUnitType','Game pieces'),"
      + "('subUnitType','Head'),"
      + "('subUnitType','Illustration'),"
      + "('subUnitType','Index'),"
      + "('subUnitType','Introduction'),"
      + "('subUnitType','Invitation'),"
      + "('subUnitType','Item'),"
      + "('subUnitType','Map'),"
      + "('subUnitType','Number'),"
      + "('subUnitType','Obverse'),"
      + "('subUnitType','Page'),"
      + "('subUnitType','Page - Additional'),"
      + "('subUnitType','Page - Additional Title'),"
      + "('subUnitType','Page - Contents '),"
      + "('subUnitType','Page - List Title'),"
      + "('subUnitType','Page - Title '),"
      + "('subUnitType','Page - Unnumbered'),"
      + "('subUnitType','Part'),"
      + "('subUnitType','Plate'),"
      + "('subUnitType','Plate - Leaf'),"
      + "('subUnitType','Plate - Verso'),"
      + "('subUnitType','Plates - List'),"
      + "('subUnitType','Port'),"
      + "('subUnitType','Preface'),"
      + "('subUnitType','Program'),"
      + "('subUnitType','Recto'),"
      + "('subUnitType','Reverse'),"
      + "('subUnitType','Section'),"
      + "('subUnitType','Series'),"
      + "('subUnitType','Session'),"
      + "('subUnitType','Spine'),"
      + "('subUnitType','State'),"
      + "('subUnitType','Subseries'),"
      + "('subUnitType','Table Of Contents'),"
      + "('subUnitType','Tail'),"
      + "('subUnitType','Text block'),"
      + "('subUnitType','Tile'),"
      + "('subUnitType','Title page verso'),"
      + "('subUnitType','Tour'),"
      + "('subUnitType','Verso'),"
      + "('subUnitType','Volume'),"
      + "('form','Book'),"
      + "('form','Manuscript'),"
      + "('form','Map'),"
      + "('form','Music'),"
      + "('form','Picture'),"
      + "('form','Serial'),"
      + "('form','Sound recording'),"
      + "('form','Other - Australian'),"
      + "('form','Other - General'),"
      + "('form','Internal photograph'),"
      + "('form','Conservation'),"
      + "('bibLevel','Set'),"
      + "('bibLevel','Item'),"
      + "('bibLevel','Part'),"
      + "('digitalStatus','Captured'),"
      + "('digitalStatus','Not Captured'),"
      + "('digitalStatus','Partially Captured'),"
      + "('digitalStatus','Preserved analogue'),"
      + "('encodingLevel','Master Copy'),"
      + "('encodingLevel','# - Full level'),"
      + "('encodingLevel','1 - Full level, material not examined'),"
      + "('encodingLevel','2 - Less-than-full level, material not examined'),"
      + "('encodingLevel','3 - Abbreviated level'),"
      + "('encodingLevel','4 - Core level'),"
      + "('encodingLevel','5 - Partial (preliminary) level'),"
      + "('encodingLevel','7 - Minimal level'),"
      + "('encodingLevel','8 - Prepublication level'),"
      + "('encodingLevel','u - Unknown'),"
      + "('encodingLevel','z - Not applicable'),"
      + "('encodingLevel','None'),"
      + "('encodingLevel','Card only'),"
      + "('encodingLevel','Collection level'),"
      + "('encodingLevel','Collection in process'),"
      + "('encodingLevel','Conditions vary'),"
      + "('encodingLevel','Full in process'),"
      + "('encodingLevel','nothing'),"
      + "('genre','Interview'),"
      + "('genre','Speech'),"
      + "('genre','Conference'),"
      + "('genre','Festival'),"
      + "('genre','Lecture'),"
      + "('genre','Monologue'),"
      + "('genre','Other'),"
      + "('publicationCategory','Press Club'),"
      + "('publicationCategory','Radio'),"
      + "('publicationCategory','Commercial'),"
      + "('publicationCategory','Master Copy'),"
      + "('publicationCategory','Other Press Club'),"
      + "('publicationCategory','Radio'),"
      + "('publicationCategory','Commercial'),"
      + "('publicationCategory','Master Copy'),"
      + "('publicationCategory','Other'),"
      + "('subject','National Library - Products'),"
      + "('subject','National Library - Tapestries'),"
      + "('subject','National Library - Volunteers - [name]'),"
      + "('subject','National Library - Publications - [name]'),"
      + "('subject','National Library - Volunteers - [name]'),"
      + "('subject','National Library - Visits - [name]'),"
      + "('subject','National Library - Building - Exterior - Art'),"
      + "('subject','National Library - Building - Exterior - 1990s'),"
      + "('subject','National Library - Building - Exterior - 2000s'),"
      + "('subject','National Library - Building - Exterior - 2010s'),"
      + "('subject','National Library - Building - Interior - [name]'),"
      + "('subject','National Library - Building - Warehouses and Annexes'),"
      + "('subject','National Library - Committees - [name]'),"
      + "('subject','National Library - Conferences/Seminars/Workshops - [name]'),"
      + "('subject','National Library - Consultants and Contractors - [name]'),"
      + "('subject','National Library - Council - Group and General Photographs'),"
      + "('subject','National Library - Council - Portrait of Individual Member - [name]'),"
      + "('subject','National Library - Donors - [name]'),"
      + "('subject','National Library - Events - [name]'),"
      + "('subject','National Library - Exhibitions - [name]'),"
      + "('subject','National Library - Exhibitions - National Treasures from Australia''s Great Libraries'),"
      + "('subject','National Library - Friends'),"
      + "('subject','National Library - Harold White Fellows - [name]'),"
      + "('subject','National Library - Kenneth Myer Lecture'),"
      + "('subject','National Library - National Folk Festival Fellows - [name]'),"
      + "('subject','National Library - Norman McCann Summer Scholars - [name]'),"
      + "('subject','National Library - Oral History Interview - [name of interviewee]'),"
      + "('subject','National Library - Staff - [name]'),"
      + "('subject','[Institution] - Collection material'),"
      + "('subject','National Library - Social Club'),"
      + "('subject','Events outside the National Library - [name]'),"
      + "('constraint','Copyright'),"
      + "('constraint','Adult content'),"
      + "('constraint','Indigenous'),"
      + "('constraint','Free-of-charge'),"
      + "('constraint','Pending'),"
      + "('constraint','Written permission required'),"
      + "('constraint','Thumbnails only'),"
      + "('constraint','Closed'),"
      + "('constraint','Indigenous - male'),"
      + "('digitalStatus','Captured'),"
      + "('digitalStatus','Not Captured'),"
      + "('digitalStatus','Partially Captured'),"
      + "('digitalStatus','Preserved analogue')"
    )
    void seedKeyValueList();
    
    /*
     * tools name attributes definition
     */
    @SqlUpdate(
            "INSERT INTO lookups(id, name, attribute, value) VALUES"
            + "(1,'tools','name', 'UMAX PowerLook 2100XL'), "
            + "(2,'tools','name','Scanview ScanMate F6'), "
            + "(3,'tools','name','AGFA Arcus II'), "
            + "(4,'tools','name','AGFA DuoScan T2000XL + FotoLook'), "
            + "(5,'tools','name','Nikon Super CoolScan 4000ED'), "
            + "(6,'tools','name','PhaseOne Phase FX'), "
            + "(7,'tools','name','Sinar Macroscan'), "
            + "(8,'tools','name','Nikon D1'), "
            + "(9,'tools','name','Canon 20D'), "
            + "(10,'tools','name','Kodak ProBack') "
            )
    void seedToolsLookupsNameAttribute();
    
    /*
     * tools resolution attributes definitions
     */
    @SqlUpdate(
            "INSERT INTO lookups(id, name, attribute, value) VALUES"
            + "(1,'tools','resolution',''),"
            + "(2,'tools','resolution',''),"
            + "(3,'tools','resolution',''),"
            + "(4,'tools','resolution',''),"
            + "(5,'tools','resolution',''),"
            + "(6,'tools','resolution',''),"
            + "(7,'tools','resolution',''),"
            + "(8,'tools','resolution',''),"
            + "(9,'tools','resolution',''),"
            + "(10,'tools','resolution','')"
            )
    void seedToolsLookupsResolutionAttribute();
    
    /*
     * tools serial number attributes definitions
     */
    @SqlUpdate(
            "INSERT INTO lookups(id, name, attribute, value) VALUES"
            + "(1,'tools','serialNumber',' R04/19825 and N04/83 of file NLA/15811 relate.'),"
            + "(2,'tools','serialNumber',''),"
            + "(3,'tools','serialNumber',''),"
            + "(4,'tools','serialNumber',''),"
            + "(5,'tools','serialNumber',''),"
            + "(6,'tools','serialNumber',''),"
            + "(7,'tools','serialNumber',''),"
            + "(8,'tools','serialNumber',''),"
            + "(9,'tools','serialNumber',''),"
            + "(10,'tools','serialNumber','')")
            void seedToolsLookupsSerialAttribute();
 
    /*
     * tools notes attribute definitions
     */
    @SqlUpdate(
            "INSERT INTO lookups(id, name, attribute, value) VALUES"
            + "(1,'tools','notes','Used MagicScan software for image capture. NLA  asset 26105 written off and disposed of in 2003/4 FY due to breakdown. TRIM folios R04/14022'),"
            + "(2,'tools','notes',''),"
            + "(3,'tools','notes',''),"
            + "(4,'tools','notes',''),"
            + "(5,'tools','notes',''),"
            + "(6,'tools','notes',''),"
            + "(7,'tools','notes',''),"
            + "(8,'tools','notes',''),"
            + "(9,'tools','notes',''),"
            + "(10,'tools','notes','')" 
            )
    void seedToolsLookupsNotesAttribute();

    @SqlUpdate(
            "INSERT INTO lookups(id, name, value) VALUES"
                    + "(451, 'toolType', 'Transmission scanner'),"
                    + "(452, 'toolType', 'Reflective scanner')"
              )
    void seedToolTypesLookups();
    
    @SqlUpdate(
            "INSERT INTO lookups(id, name, code, value) VALUES"
            + "(499, 'toolCategory', 'd', 'Device'),"
            + "(500, 'toolCategory', 's', 'Software')"        
            )
    void seedToolCategoriesLookups();
    
    @SqlUpdate(
            "INSERT INTO lookups(id, name, value) VALUES"
            + "(501, 'materialType', 'Image'),"
            + "(502, 'materialType', 'Sound'),"
            + "(503, 'materialType', 'Text')"
            )
    void seedMaterialTypesLookups();
    
    @SqlUpdate(
            "INSERT INTO maps(id, parent_id) VALUES"
            + "(1,null),"
            + "(2,451),"
            + "(3,451),"
            + "(4,451),"
            + "(5,451),"
            + "(6,452),"
            + "(7,452),"
            + "(8,452),"
            + "(9,452),"
            + "(10,452)"
            )
     void seedToolTypesMaps();
    
    @SqlUpdate(
            "INSERT INTO maps(id, parent_id) VALUES"
            + "(9, 499),"
            + "(451, 499),"
            + "(452, 499),"
            + "(453, 499),"
            + "(454, 499),"
            + "(455, 500),"
            + "(456, 500)"
            )
    void seedToolCategoriesMaps();
    
    @SqlUpdate(
            "INSERT INTO maps(id, parent_id) VALUES"
            + "(9, 501),"
            + "(451, 501),"
            + "(452, 501),"
            + "(453, 501),"
            + "(454, 501),"
            + "(455, 501),"
            + "(456, 501)"
            )
     void seedToolMaterialTypesMaps();

    
    /*
     * Main table indexes - these require review as they might need indexes.
     */
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_vert "
            + "ON vertex(id, txn_start)")
    void createVertexIndex();

    
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_edge "
            + "ON edge(id, txn_start)")
    void createEdgeIndex();

    
    @SqlUpdate(
            "CREATE UNIQUE INDEX unique_prop "
            + "ON property(id, txn_start, name)")
    void createPropertyIndex();

    
    @SqlUpdate(
            "CREATE INDEX edge_in_idx "
            + "ON edge(v_in)")
    void createEdgeInVertexIndex();

    
    @SqlUpdate(
            "CREATE INDEX edge_out_idx "
            + "ON edge(v_out)")
    void createEdgeOutVertexIndex();

    
    @SqlQuery(
            "SELECT (COUNT(table_name) = 8) "
            + "FROM information_schema.tables " 
            + "WHERE table_name IN ("
            + "  'VERTEX', 'EDGE', 'PROPERTY', "
            + "  'SESS_VERTEX', 'SESS_EDGE', 'SESS_PROPERTY', "
            + "  'ID_GENERATOR', 'TRANSACTION')")
    boolean schemaTablesExist();
    
    
    @SqlUpdate(
            "DROP TABLE IF EXISTS "
            + "vertex, edge, property, "
            + "sess_vertex, sess_edge, sess_property, "
            + "transaction, id_generator")
    void dropTables();

    
    /*
     * id generation operations
     */
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO id_generator () "
            + "VALUES ()")
    long newId();

    
    @SqlUpdate("DELETE "
            + "FROM id_generator "
            + "WHERE id < :id")
    void garbageCollectIds(
            @Bind("id") long id);

    
    /*
     * suspend/resume operations
     */
    @SqlBatch("INSERT INTO sess_edge (s_id, id, txn_start, txn_end, v_out, v_in, label, edge_order, state) "
            + "VALUES (:sessId, :id, :txnStart, :txnEnd, :outId, :inId, :label, :edgeOrder, :state)")
    void suspendEdges(
            @Bind("sessId")    Long          sessId,
            @Bind("id")        List<Long>    id,
            @Bind("txnStart")  List<Long>    txnStart,
            @Bind("txnEnd")    List<Long>    txnEnd,
            @Bind("outId")     List<Long>    outId,
            @Bind("inId")      List<Long>    inId,
            @Bind("label")     List<String>  label,
            @Bind("edgeOrder") List<Integer> edgeOrder,
            @Bind("state")     List<String>  state);

    
    @SqlBatch("INSERT INTO sess_vertex (s_id, id, txn_start, txn_end, state) "
            + "VALUES (:sessId, :id, :txnStart, :txnEnd, :state)")
    void suspendVertices(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("txnStart")  List<Long>   txnStart,
            @Bind("txnEnd")    List<Long>   txnEnd,
            @Bind("state")     List<String> state);

    
    @SqlBatch("INSERT INTO sess_property (s_id, id, name, type, value) "
            + "VALUES (:sessId, :id, :name, :type, :value)")
    void suspendProperties(
            @Bind("sessId")    Long         sessId,
            @Bind("id")        List<Long>   id,
            @Bind("name")      List<String> name,
            @Bind("type")      List<String> type,
            @Bind("value")     List<byte[]> value);

    
    @SqlQuery("SELECT id, name, type, value "
            + "FROM sess_property "
            + "WHERE s_id = :sessId")
    @Mapper(PropertyMapper.class)
    List<AmberProperty> resumeProperties(@Bind("sessId") Long sessId);
    
    
    /* Note: resume edge and vertex implemented in AmberGraph
    
    
    /*
     * commit operations
     */
    @SqlUpdate(
            "INSERT INTO transaction (id, time, user, operation)" +
            "VALUES (:id, :time, :user, :operation)")
    void insertTransaction(
            @Bind("id") long id, 
            @Bind("time") long time, 
            @Bind("user") String user,
            @Bind("operation") String operation);

        
    @SqlUpdate("")
    void endElements(
            @Bind("txnId") Long txnId);

    
    @SqlUpdate("SET @txn = :txnId;\n"
            
            // edges            
            + "INSERT INTO edge (id, txn_start, txn_end, v_out, v_in, label, edge_order) "
            + "SELECT id, s_id, 0, v_out, v_in, label, edge_order "
            + "FROM sess_edge "
            + "WHERE s_id = @txn "
            + "AND (state = 'NEW' OR state = 'MOD');\n"

            // vertices            
            + "INSERT INTO vertex (id, txn_start, txn_end) "
            + "SELECT id, s_id, 0 "
            + "FROM sess_vertex "
            + "WHERE s_id = @txn "
            + "AND (state = 'NEW' OR state = 'MOD');\n"

            // properties            
            + "INSERT INTO property (id, txn_start, txn_end, name, type, value) "
            + "SELECT id, s_id, 0, name, type, value "
            + "FROM sess_property "
            + "WHERE s_id = @txn") 
    void startElements(
            @Bind("txnId") Long txnId);


    void close();


    @SqlUpdate("SET @sessId = :sessId;\n" +
    
            "DELETE FROM sess_vertex " +
            "WHERE s_id = @sessId;\n" +
            
            "DELETE FROM sess_edge " +
            "WHERE s_id = @sessId;\n" +
            
            "DELETE FROM sess_property " +
            "WHERE s_id = @sessId;\n")
    void clearSession(
            @Bind("sessId") Long sessId);
    
    
    @CreateSqlObject
    public abstract Lookups lookups();
}

