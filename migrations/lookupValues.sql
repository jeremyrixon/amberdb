#This file represents the initial state of the lookups table. This table is updated with new fields semi-frequently 
#so this document should not be seen as a cannonical resource.

insert into list (name, value) values ('materialType', 'Image'), ('materialType', 'Sound'), ('materialType', 'Text' );
insert into list (name, value) values ('copyType', 'Physical'), ('copyType', 'Digitised'), ('copyType', 'Born Digital');

insert into list (name, value) values 
('copyRole', 'Related metadata'),
('copyRole', 'RealMedia reference'),
('copyRole', 'RealMedia file'),
('copyRole', 'QuickTime reference 1'),
('copyRole', 'QuickTime reference 2'),
('copyRole', 'QuickTime reference 3'),
('copyRole', 'QuickTime reference 4'),
('copyRole', 'QuickTime file 1'),
('copyRole', 'QuickTime file 2'),
('copyRole', 'Original'),
('copyRole', 'Thumbnail'),
('copyRole', 'View'),
('copyRole', 'Examination'),
('copyRole', 'Master'),
('copyRole', 'Co-master'),
('copyRole', 'Archive'),
('copyRole', 'Summary'),
('copyRole', 'Transcript'),
('copyRole', 'Structural map'),
('copyRole', 'Finding aid'),
('copyRole', 'Microcassette'),
('copyRole', 'Special delivery'),
('copyRole', 'Listen 1'),
('copyRole', 'Listen 2'),
('copyRole', 'Listen 3'),
('copyRole', 'Finding aid print'),
('copyRole', 'List'),
('copyRole', 'Derivative master'),
('copyRole', 'Analogue distribution'),
('copyRole', 'Working'),
('copyRole', 'Digital distribution'),
('copyRole', 'Time-coded Transcript'),
('copyRole', 'Print');

insert into list (name, value) values 
('carrier', 'File system'),
('carrier', 'Working Reel'),
('carrier', 'User Cassette'),
('carrier', 'Online'),
('carrier', 'CD-R'),
('carrier', 'CD-ROM'),
('carrier', 'Second copy'),
('carrier', 'CD Archive'),
('carrier', 'CD Filtered'),
('carrier', 'Preservation Reel'),
('carrier', 'Safety DAT'),
('carrier', 'Cassette A'),
('carrier', 'Cassette B'),
('carrier', 'DAT'),
('carrier', 'Multitrack-ADAT'),
('carrier', 'Multitrack-DTRS'),
('carrier', 'Reel A'),
('carrier', 'Reel B'),
('carrier', 'CD Duplicate'),
('carrier', 'User CD'),
('carrier', 'Second Duplicate'),
('carrier', 'DVD Archive'),
('carrier', 'Safety Reel'),
('carrier', 'Microcassette'),
('carrier', 'NONE');

insert into list (name, value) values
('acquisitionStatus', 'CURRENT'),
('acquisitionStatus', 'BACKLOG'),
('acquisitionStatus', 'MS');

insert into list (name, value) values
('acquisitionCategory', 'Social History'),
('acquisitionCategory', 'Eminent Australians'),
('acquisitionCategory', 'Folklore'),
('acquisitionCategory', 'Others');

insert into list (name, value) values
('segmentIndicator', 'Non-timed'),
('segmentIndicator', 'Timed');

insert into list (name, value) values
('bitDepth', '8'),
('bitDepth', '12'),
('bitDepth', '14'),
('bitDepth', '16'),
('bitDepth', '18'),
('bitDepth', '20'),
('bitDepth', '22'),
('bitDepth', '24');

insert into list (name, value) values
('orientation', 'Portrait'),
('orientation', 'LandscapePortrait'),
('orientation', 'Landscape');

insert into list (name, value) values
('colourProfile', 'Within image'),
('colourProfile', 'External');

insert into list (name, value) values
('surface', 'Enhanced Azo'),
('surface', 'C-CrO2'),
('surface', 'C-Fe2O3'),
('surface', 'C-Metal'),
('surface', 'Cyanine'),
('surface', 'DOSS'),
('surface', 'Metal Azo'),
('surface', 'Pthalocyanine'),
('surface', 'Pthalocyanine Al T-Acetate'),
('surface', 'T-Paper'),
('surface', 'T-Polyester'),
('surface', 'T-PVC'),
('surface', 'Unsure');

insert into list (name, value) values
('carrierDuration', '2 min'),
('carrierDuration', '5 min'),
('carrierDuration', '12 min'),
('carrierDuration', '30 min'),
('carrierDuration', '40 min'),
('carrierDuration', '45 min'),
('carrierDuration', '46 min'),
('carrierDuration', '60 min'),
('carrierDuration', '63 min'),
('carrierDuration', '65 min'),
('carrierDuration', '74 min'),
('carrierDuration', '80 min'),
('carrierDuration', '90 min'),
('carrierDuration', '95 min'),
('carrierDuration', '96 min'),
('carrierDuration', '120 min'),
('carrierDuration', '122 min'),
('carrierDuration', '125 min'),
('carrierDuration', '180 min'),
('carrierDuration', '650 MB'),
('carrierDuration', '4.7 GB');

insert into list (name, value) values
('reelSize', '2in'),
('reelSize', '3in IEC'),
('reelSize', '4in IEC'),
('reelSize', '5in IEC'),
('reelSize', '5in NAB'),
('reelSize', '6in IEC'),
('reelSize', '7in IEC'),
('reelSize', '7in NAB'),
('reelSize', '8.25in IEC'),
('reelSize', '10in NAB'),
('reelSize', '10in IEC');

insert into list (name, value) values
('channel', '0.5'),
('channel', '1'),
('channel', '2'),
('channel', '4'),
('channel', '8'),
('channel', '16'),
('channel', '24');

insert into list (name, value) values
('speed', '2.38 cm/s'),
('speed', '4.76 cm/s'),
('speed', '9.5 cm/s'),
('speed', '19.05 cm/s'),
('speed', '38.1 cm/s'),
('speed', '76.2 cm/s');

insert into list (name, value) values
('thickness', 'Double play'),
('thickness', 'Long play'),
('thickness', 'Standard'),
('thickness', 'Triple play');

insert into list (name, value) values
('brand', 'EMITAPE 77/6'),
('brand', 'TDK 150'),
('brand', 'Scotch 131'),
('brand', 'AGFA PE 46'),
('brand', 'ACV'),
('brand', 'admark'),
('brand', '3M 807'),
('brand', 'ACME'),
('brand', 'AGFA'),
('brand', 'AGFA CARAT FeCr'),
('brand', 'AGFA DA DAT'),
('brand', 'AGFA DAT'),
('brand', 'AGFA FeI-S'),
('brand', 'AGFA FERRO COLOR'),
('brand', 'AGFA FERROCOLOR'),
('brand', 'AGFA FS'),
('brand', 'AGFA FS 396'),
('brand', 'AGFA GEVAERT'),
('brand', 'AGFA -GEVAERT PE36'),
('brand', 'AGFA Magnetonband'),
('brand', 'AGFA Magnetonband PER 525'),
('brand', 'AGFA PE 31'),
('brand', 'AGFA PE 36'),
('brand', 'AGFA PE 38'),
('brand', 'AGFA PE 4'),
('brand', 'AGFA PE 41'),
('brand', 'AGFA PE 65'),
('brand', 'AGFA PEM 369'),
('brand', 'AGFA PEM 468'),
('brand', 'AGFA PEM 469'),
('brand', 'AGFA PER'),
('brand', 'AGFA PER 368'),
('brand', 'AGFA PER 468'),
('brand', 'AGFA PER 525'),
('brand', 'AGFA PER 528'),
('brand', 'AGFA PER 555'),
('brand', 'AGFA SM'),
('brand', 'AGFA SUPER C60+6'),
('brand', 'AGFA SUPER COLOR'),
('brand', 'AGFA SUPERCHROM'),
('brand', 'AGFA WOLFEN'),
('brand', 'AGFA-GEVAERT'),
('brand', 'AGFA-GEVAERT 65'),
('brand', 'AGFA-GEVAERT SM'),
('brand', 'AIWA'),
('brand', 'AIWA DAT'),
('brand', 'AKAI'),
('brand', 'AKAI AT-5S'),
('brand', 'AKAI Ferro Extra I'),
('brand', 'AKAI HX'),
('brand', 'Allegro'),
('brand', 'ALMAG'),
('brand', 'ALPHA'),
('brand', 'AMCOLOR'),
('brand', 'AMERICAN'),
('brand', 'American Recording tape'),
('brand', 'AMPEX'),
('brand', 'AMPEX 1800'),
('brand', 'AMPEX 2020'),
('brand', 'AMPEX 2500'),
('brand', 'AMPEX 350'),
('brand', 'AMPEX 370'),
('brand', 'AMPEX 406'),
('brand', 'AMPEX 407'),
('brand', 'AMPEX 431'),
('brand', 'AMPEX 444'),
('brand', 'AMPEX 456'),
('brand', 'AMPEX 457'),
('brand', 'AMPEX 467 DAT'),
('brand', 'AMPEX 478'),
('brand', 'AMPEX 511'),
('brand', 'AMPEX 521'),
('brand', 'AMPEX 531'),
('brand', 'AMPEX 600'),
('brand', 'Ampex 611'),
('brand', 'AMPEX 631'),
('brand', 'AMPEX 632'),
('brand', 'AMPEX 641'),
('brand', 'AMPEX 642'),
('brand', 'AMPEX 679'),
('brand', 'AMPEX DAT'),
('brand', 'AMPEX EDR'),
('brand', 'AMPEX ELN'),
('brand', 'AMPEX GM'),
('brand', 'AMPEX GM3600'),
('brand', 'AMPEX GMI'),
('brand', 'AMPEX PLUS'),
('brand', 'AMPEX UDR'),
('brand', 'Anorgana Geno 1on Typ'),
('brand', 'Aristocrat'),
('brand', 'ARMSTRONG AUDIO'),
('brand', 'Astropulse'),
('brand', 'AUDIO DIMENSION'),
('brand', 'Audio Magnetics'),
('brand', 'AUDIO MAGNETICS CORPORATION'),
('brand', 'AUDIOMASTERS CORPORATION'),
('brand', 'AUDIOSONIC'),
('brand', 'audiotape'),
('brand', 'audiotape Q19'),
('brand', 'AUSTRALIAN HANIMEX'),
('brand', 'AVC'),
('brand', 'Award'),
('brand', 'BASF'),
('brand', 'BASF Chromdioxid'),
('brand', 'BASF chromdioxid super'),
('brand', 'BASF Chromdioxid Super II'),
('brand', 'BASF Chrome Extra II'),
('brand', 'BASF Chrome MaximaII'),
('brand', 'BASF CHROME SUPER II'),
('brand', 'BASF CR-M II'),
('brand', 'BASF DAT'),
('brand', 'BASF DP'),
('brand', 'BASF DP 26'),
('brand', 'BASF DP 26 LH'),
('brand', 'BASF DP26'),
('brand', 'BASF Ferro Extra I'),
('brand', 'BASF ferro maxima I'),
('brand', 'BASF Ferro Super I'),
('brand', 'BASF Ferro Super LH I'),
('brand', 'BASF ferro super LHI'),
('brand', 'BASF hifi'),
('brand', 'BASF LANGSPIELBAND'),
('brand', 'BASF LGH'),
('brand', 'BASF LGH 30 P'),
('brand', 'BASF LGR'),
('brand', 'BASF LGR 30 P'),
('brand', 'BASF LGR 35'),
('brand', 'BASF LGR P'),
('brand', 'BASF LGR P 30'),
('brand', 'BASF LGS'),
('brand', 'BASF LGS 26'),
('brand', 'BASF LGS 35'),
('brand', 'BASF LGS 52'),
('brand', 'BASF LH'),
('brand', 'BASF LH 90'),
('brand', 'BASF LH extra I'),
('brand', 'BASF LH SM'),
('brand', 'BASF LH Super'),
('brand', 'BASF LH SUPER 1'),
('brand', 'BASF LH Super I'),
('brand', 'BASF LH-EI'),
('brand', 'BASF LH-MI'),
('brand', 'BASF LHSM'),
('brand', 'BASF LN'),
('brand', 'BASF LN super 1'),
('brand', 'BASF LN super I'),
('brand', 'BASF LP'),
('brand', 'BASF LP35'),
('brand', 'BASF LPR 35'),
('brand', 'BASF LR 56'),
('brand', 'BASF PEM 369'),
('brand', 'BASF PEM 468'),
('brand', 'BASF PER 368'),
('brand', 'BASF PES 18'),
('brand', 'BASF SM'),
('brand', 'BASF SM 468'),
('brand', 'BASF SM 911'),
('brand', 'BASF SP 52'),
('brand', 'BASF SP50 LH'),
('brand', 'BASF SP54 R'),
('brand', 'BASF SP55 R'),
('brand', 'BASF SPR 50'),
('brand', 'BASF SPR 50 LH'),
('brand', 'BASF SPR 50 LHL'),
('brand', 'BASF TP 18 LH'),
('brand', 'BASF TYP LGN'),
('brand', 'BASF TYP LGS'),
('brand', 'BASF Typ LGS 26'),
('brand', 'BASF TYP LGS 52'),
('brand', 'BEL CLEER'),
('brand', 'BITCO'),
('brand', 'Brand 5'),
('brand', 'Brand Five'),
('brand', 'CAPITOL'),
('brand', 'certron'),
('brand', 'certron HE90'),
('brand', 'CFS Westinghouse-SPRcolor'),
('brand', 'Chapple'),
('brand', 'Compact Cassette'),
('brand', 'CONCERTAPE'),
('brand', 'Concorde'),
('brand', 'CONTRAST'),
('brand', 'CORONET HE'),
('brand', 'CORONET LN'),
('brand', 'Craig'),
('brand', 'CREST INTERNATIONAL'),
('brand', 'Crystal'),
('brand', 'cts'),
('brand', 'DENON'),
('brand', 'DENON DX'),
('brand', 'DENON DX1'),
('brand', 'DENON DX8'),
('brand', 'DENON LX'),
('brand', 'DEUTSCHE WELLE'),
('brand', 'Dick Smith'),
('brand', 'DICK SMITH EDR'),
('brand', 'Dindy'),
('brand', 'Dindy Black'),
('brand', 'Dindy Super'),
('brand', 'Dokorder'),
('brand', 'Dominion'),
('brand', 'Douglas Hi-Fi'),
('brand', 'duCros'),
('brand', 'EASTMAN KODAK'),
('brand', 'E-LITE'),
('brand', 'EMI'),
('brand', 'EMI Hi dynamic'),
('brand', 'EMITAPE'),
('brand', 'EMITAPE 100'),
('brand', 'EMITAPE 4'),
('brand', 'EMITAPE 5'),
('brand', 'EMITAPE 88'),
('brand', 'EMITAPE 88/12EH'),
('brand', 'EMITAPE 88/6'),
('brand', 'EMITAPE Afonic'),
('brand', 'EMITAPE HI-DYNAMIC'),
('brand', 'EMITAPE HI-DYNAMIC 9'),
('brand', 'EMITAPE HI-DYNAMIC HLP-9'),
('brand', 'EMITAPE HI-DYNAMIC HSP6'),
('brand', 'EMITAPE MAGNETIC'),
('brand', 'EMITAPE X1000'),
('brand', 'ferro MASTER UD'),
('brand', 'Ferrodynamics'),
('brand', 'Ferrograph-Ampex'),
('brand', 'FORWARD'),
('brand', 'FUJI'),
('brand', 'FUJI DAT'),
('brand', 'FUJI DR'),
('brand', 'FUJI DR-I'),
('brand', 'FUJI FILM'),
('brand', 'FUJI FILM FL'),
('brand', 'FUJI FILM FX'),
('brand', 'FUJI FL'),
('brand', 'FUJI FR-I'),
('brand', 'FUJI FR-I Super'),
('brand', 'FUJI FX-II'),
('brand', 'FUJI R DAT'),
('brand', 'Geloso'),
('brand', 'Gevaert'),
('brand', 'glatigny'),
('brand', 'GOLDRING'),
('brand', 'GOLDRING STUDIO RANGE'),
('brand', 'Graceline'),
('brand', 'GREENCORP'),
('brand', 'GRUNDIG'),
('brand', 'Grundig TLP2'),
('brand', 'GTape'),
('brand', 'GTape 900'),
('brand', 'GTape High Density'),
('brand', 'HANIMEX'),
('brand', 'HCL'),
('brand', 'HHB'),
('brand', 'HHb DA113DC'),
('brand', 'HITACHI'),
('brand', 'HITACHI DL'),
('brand', 'HITACHI Lo-D'),
('brand', 'HITACHI UD'),
('brand', 'HITACHI ULTRA'),
('brand', 'HITACHI ULTRA DYNAMIC'),
('brand', 'HI-TECH'),
('brand', 'ILFORD'),
('brand', 'imation'),
('brand', 'imitation'),
('brand', 'INTERNATIONAL'),
('brand', 'IRISH'),
('brand', 'IRU'),
('brand', 'KDK'),
('brand', 'KGC'),
('brand', 'KLARION'),
('brand', 'KODAK'),
('brand', 'KODAVOX'),
('brand', 'LAFAYETTE'),
('brand', 'Lancia'),
('brand', 'Lectra'),
('brand', 'LOEWE OPTA'),
('brand', 'M R SOUNDS'),
('brand', 'magna MAX2'),
('brand', 'MAGNASOUND'),
('brand', 'MALLORY'),
('brand', 'MARSHAL GOLDEN STUDIO'),
('brand', 'Marshall'),
('brand', 'MASTERTAPE'),
('brand', 'MASTERTONE'),
('brand', 'MAXELL'),
('brand', 'Maxell 35-90B'),
('brand', 'MAXELL A35.5'),
('brand', 'MAXELL A35-7'),
('brand', 'Maxell A50.5'),
('brand', 'maxell E25-7'),
('brand', 'Maxell LN'),
('brand', 'maxell MX'),
('brand', 'maxell S-LN'),
('brand', 'maxell UD'),
('brand', 'maxell UDI'),
('brand', 'maxell UDS-II'),
('brand', 'maxell UL'),
('brand', 'maxell UR'),
('brand', 'maxell XL II'),
('brand', 'maxell XLI'),
('brand', 'maxell XLI-S'),
('brand', 'MAX-WELL'),
('brand', 'Maxwell MX'),
('brand', 'Melody'),
('brand', 'MEMOREX'),
('brand', 'MEMOREX 1800'),
('brand', 'MEMOREX MRX 2'),
('brand', 'MEMOREX MRX I'),
('brand', 'mfp'),
('brand', 'MIDAS'),
('brand', 'MITAPE'),
('brand', 'MUSICWAY AUDIO'),
('brand', 'NAGU'),
('brand', 'NATIONAL'),
('brand', 'National EN'),
('brand', 'NATIONAL PANASONIC'),
('brand', 'NATIONAL PANASONIC RT'),
('brand', 'NATIONAL RT'),
('brand', 'NATIONAL RT 3'),
('brand', 'NATIONAL RT 3G'),
('brand', 'NATIONAL RT 5'),
('brand', 'NATIONAL RT-5G'),
('brand', 'NATIONAL RT-7G'),
('brand', 'No Frills'),
('brand', 'NORD'),
('brand', 'NORD BC'),
('brand', 'NORDIC'),
('brand', 'OCL'),
('brand', 'Olympic Technology'),
('brand', 'OPUS'),
('brand', 'OPUS UD'),
('brand', 'OPUS XD1'),
('brand', 'Pacific'),
('brand', 'PARAMOUNT'),
('brand', 'PDQ'),
('brand', 'PENNCREST'),
('brand', 'PERMATION'),
('brand', 'PERMATON'),
('brand', 'PHILIPS'),
('brand', 'PHILIPS AV'),
('brand', 'PHILIPS DP 13'),
('brand', 'PHILIPS DP 18'),
('brand', 'PHILIPS DP13'),
('brand', 'PHILIPS EL 3915'),
('brand', 'PHILIPS EL3914'),
('brand', 'PHILIPS EL3915'),
('brand', 'PHILIPS ER 13'),
('brand', 'PHILIPS ER 18'),
('brand', 'PHILIPS FE'),
('brand', 'PHILIPS FE I'),
('brand', 'PHILIPS FS'),
('brand', 'PHILIPS LP 10'),
('brand', 'PHILIPS LP 13'),
('brand', 'PHILIPS LP 15'),
('brand', 'PHILIPS LP 18'),
('brand', 'PRINZ'),
('brand', 'PRO DISC'),
('brand', 'PULSAR'),
('brand', 'PURVISONIC'),
('brand', 'Philips SP18'),
('brand', 'Pinnacle'),
('brand', 'PYRAL'),
('brand', 'PYROX'),
('brand', 'PYROX MAGICTAPE'),
('brand', 'QANTEM'),
('brand', 'QUANTEGY'),
('brand', 'RADIANT'),
('brand', 'Radio Shack'),
('brand', 'rainbow'),
('brand', 'Ralec'),
('brand', 'RALMAR'),
('brand', 'RALMER'),
('brand', 'RCA'),
('brand', 'RCA RED SEAL'),
('brand', 'REALISTIC'),
('brand', 'REALISTIC GOLD'),
('brand', 'REALISTIC LN'),
('brand', 'REALISTIC SUPERTAPE GOLD'),
('brand', 'recoton'),
('brand', 'Red Seal'),
('brand', 'ROBINS'),
('brand', 'ROLA'),
('brand', 'ROLATAPE'),
('brand', 'ROSS'),
('brand', 'ROYAL APC'),
('brand', 'RTC Playrite'),
('brand', 'RZ'),
('brand', 'SAISHO'),
('brand', 'SANYO'),
('brand', 'SAST'),
('brand', 'Scotch'),
('brand', 'Scotch 102'),
('brand', 'Scotch 111'),
('brand', 'Scotch 111A'),
('brand', 'Scotch 150'),
('brand', 'Scotch 175'),
('brand', 'Scotch 176'),
('brand', 'Scotch 177'),
('brand', 'Scotch 190'),
('brand', 'Scotch 200'),
('brand', 'Scotch 202'),
('brand', 'Scotch 203'),
('brand', 'Scotch 206'),
('brand', 'Scotch 207'),
('brand', 'Scotch 208'),
('brand', 'Scotch 215'),
('brand', 'Scotch 250'),
('brand', 'Scotch 9008'),
('brand', 'Scotch AV 177'),
('brand', 'Scotch Boy'),
('brand', 'Scotch BX'),
('brand', 'Scotch CLASSIC'),
('brand', 'Scotch DYNARANGE'),
('brand', 'Scotch Master'),
('brand', 'Scotch RB-5'),
('brand', 'Shamrock'),
('brand', 'SHARP'),
('brand', 'SILVER SONIC'),
('brand', 'silver sound'),
('brand', 'Silver TRAK UDX'),
('brand', 'Soni-Tape'),
('brand', 'SONOCOLOR'),
('brand', 'SONY'),
('brand', 'SONY 100'),
('brand', 'SONY 60'),
('brand', 'SONY AHF'),
('brand', 'SONY BHF'),
('brand', 'SONY C'),
('brand', 'SONY CHF'),
('brand', 'SONY DAT'),
('brand', 'SONY DC'),
('brand', 'SONY DT DAT'),
('brand', 'SONY EF'),
('brand', 'SONY HF'),
('brand', 'SONY PDP-65'),
('brand', 'SONY PDP-65C'),
('brand', 'SONY PR-150'),
('brand', 'SONY SUPER 150'),
('brand', 'SONY SUPER 300'),
('brand', 'SONY UCX'),
('brand', 'SONY UCX-S'),
('brand', 'SONY ULH'),
('brand', 'SONY UX'),
('brand', 'SONY UX-S'),
('brand', 'SONY ZX'),
('brand', 'SOUNDCRAFT'),
('brand', 'Star'),
('brand', 'STUDER'),
('brand', 'SUMMIT'),
('brand', 'Sunhing'),
('brand', 'SUPERTAPE GOLD'),
('brand', 'SUPERTAPE HD'),
('brand', 'SWEDA'),
('brand', 'T.D.J'),
('brand', 'TAMMY'),
('brand', 'tandberg radio'),
('brand', 'TARGET'),
('brand', 'TDK'),
('brand', 'TDK 90'),
('brand', 'TDK AD'),
('brand', 'TDK ADX'),
('brand', 'TDK AR'),
('brand', 'TDK ARX'),
('brand', 'TDK C'),
('brand', 'TDK D'),
('brand', 'TDK DA-R DAT'),
('brand', 'TDK DA-RXG'),
('brand', 'TDK F'),
('brand', 'TDK LN'),
('brand', 'TDK LX 35'),
('brand', 'TDK MA-R'),
('brand', 'TDK MA-X'),
('brand', 'TDK OD'),
('brand', 'TDK RC2'),
('brand', 'TDK SA'),
('brand', 'TDK SA-X'),
('brand', 'TDK SD'),
('brand', 'TDK SF'),
('brand', 'TDK Super Dynamic 1200 - SD'),
('brand', 'That''s'),
('brand', 'TDK Super Dynamic 1200-SD'),
('brand', 'TDK-E Synchroreel'),
('brand', 'TEAC'),
('brand', 'TEAC SOUND 52'),
('brand', 'TELEX'),
('brand', 'TEMPEST'),
('brand', 'That''s'),
('brand', 'thunder'),
('brand', 'TIANTAN'),
('brand', 'TONEX'),
('brand', 'TONEX Pastels'),
('brand', 'Toshiba'),
('brand', 'Tower'),
('brand', 'TRANSONIC'),
('brand', 'tronictape'),
('brand', 'TSL D'),
('brand', 'Ultra Sound'),
('brand', 'Unic'),
('brand', 'Unknown'),
('brand', 'VERBATIM'),
('brand', 'VIbrant'),
('brand', 'VIVA'),
('brand', 'W & G'),
('brand', 'WHSMITH'),
('brand', 'WORLD SLN'),
('brand', 'YASHIMA UFOI'),
('brand', 'ZENITH'),
('brand', 'EMITAPE 99'),
('brand', 'TEAC CDX'),
('brand', 'Dictasette'),
('brand', 'Zonal'),
('brand', 'TDK GX 35-90B'),
('brand', 'AMPEX 541'),
('brand', 'AMPEX 434'),
('brand', 'ORWO CR 35'),
('brand', 'Scotch RB-7'),
('brand', 'Scotch 220'),
('brand', 'PHILLIPS CD'),
('brand', 'ORWO'),
('brand', 'EMI Super'),
('brand', 'esc'),
('brand', 'BASF Ferro POWER I'),
('brand', 'TDK LX 50-60B'),
('brand', 'JVC MC-60SF');

insert into list (name, value) values
('durationType', 'Default'),
('durationType', 'Input');

insert into list (name, value) values
('equalisation', 'IEC'),
('equalisation', 'NAB');

insert into list (name, value) values
('samplingRate', '11000'),
('samplingRate', '22050'),
('samplingRate', '24000'),
('samplingRate', '32000'),
('samplingRate', '44100'),
('samplingRate', '48000'),
('samplingRate', '88200'),
('samplingRate', '96000'),
('samplingRate', '176400'),
('samplingRate', '192000');

insert into list (name, value) values
('soundField', 'Stereo'),
('soundField', 'Mono');

insert into list (name, value) values
('collection','Australian'),
('collection','Manuscript'),
('collection','Map'),
('collection','Music'),
('collection','Picture'),
('collection','General'),
('collection','Oral history and folklore'),
('collection','Internal photograph'),
('collection','Conservation');

insert into list (name, value) values
('subUnitType','Additional Material'),
('subUnitType','Attachment'),
('subUnitType','Bibliography'),
('subUnitType','Biography'),
('subUnitType','Board leaf'),
('subUnitType','Book'),
('subUnitType','Booklet'),
('subUnitType','Box'),
('subUnitType','Chapter'),
('subUnitType','Clipping'),
('subUnitType','Cover'),
('subUnitType','Cover - Back'),
('subUnitType','Cover - Front'),
('subUnitType','Cover - Inside Front '),
('subUnitType','Cover - Variant'),
('subUnitType','Cover - Inside Back '),
('subUnitType','Dedication'),
('subUnitType','Discography'),
('subUnitType','Enclosure'),
('subUnitType','End Papers'),
('subUnitType','Envelope'),
('subUnitType','Fly Leaf'),
('subUnitType','Folder'),
('subUnitType','Folio'),
('subUnitType','Foredge'),
('subUnitType','Game board'),
('subUnitType','Game instructions'),
('subUnitType','Game pieces'),
('subUnitType','Head'),
('subUnitType','Illustration'),
('subUnitType','Index'),
('subUnitType','Introduction'),
('subUnitType','Invitation'),
('subUnitType','Item'),
('subUnitType','Map'),
('subUnitType','Number'),
('subUnitType','Obverse'),
('subUnitType','Page'),
('subUnitType','Page - Additional'),
('subUnitType','Page - Additional Title'),
('subUnitType','Page - Contents '),
('subUnitType','Page - List Title'),
('subUnitType','Page - Title '),
('subUnitType','Page - Unnumbered'),
('subUnitType','Part'),
('subUnitType','Plate'),
('subUnitType','Plate - Leaf'),
('subUnitType','Plate - Verso'),
('subUnitType','Plates - List'),
('subUnitType','Port'),
('subUnitType','Preface'),
('subUnitType','Program'),
('subUnitType','Recto'),
('subUnitType','Reverse'),
('subUnitType','Section'),
('subUnitType','Series'),
('subUnitType','Session'),
('subUnitType','Spine'),
('subUnitType','State'),
('subUnitType','Subseries'),
('subUnitType','Table Of Contents'),
('subUnitType','Tail'),
('subUnitType','Text block'),
('subUnitType','Tile'),
('subUnitType','Title page verso'),
('subUnitType','Tour'),
('subUnitType','Verso'),
('subUnitType','Volume');

insert into list (name, value) values
('form','Book'),
('form','Manuscript'),
('form','Map'),
('form','Music'),
('form','Picture'),
('form','Serial'),
('form','Sound recording'),
('form','Other - Australian'),
('form','Other - General'),
('form','Internal photograph'),
('form','Conservation');

insert into list (name, value) values
('bibLevel','Set'),
('bibLevel','Item'),
('bibLevel','Part');

insert into list (name, value) values
('digitalStatus','Captured'),
('digitalStatus','Not Captured'),
('digitalStatus','Partially Captured'),
('digitalStatus','Preserved analogue');