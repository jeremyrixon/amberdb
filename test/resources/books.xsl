<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
  <xsl:output method="xml" encoding="utf-8" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <!-- ========================= -->
  <!-- root element: books       -->
  <!-- ========================= -->
  <xsl:template match="books">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="simpleA4" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="simpleA4">
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-size="10pt">
            <fo:table table-layout="fixed" width="100%" border-collapse="separate">
              <fo:table-column column-width="4cm"/>
              <fo:table-column column-width="4cm"/>
              <fo:table-column column-width="5cm"/>
              <fo:table-body>
  <!-- ========================= -->
  <!-- book list heading         -->
  <!-- ========================= -->
                <fo:table-row>
                  <xsl:attribute name="font-weight">bold</xsl:attribute>
                  <fo:table-cell>
                     <fo:block>
                     Book Title
                     </fo:block>
                  </fo:table-cell>
                  <fo:table-cell>
                     <fo:block>
                     Author
                     </fo:block>
                  </fo:table-cell>
                </fo:table-row>
                <xsl:apply-templates select="book"/>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  <!-- ========================= -->
  <!-- child element: book       -->
  <!-- ========================= -->
  <xsl:template match="book">
    <fo:table-row>
      <xsl:if test="title/@read = 'y'">
        <xsl:attribute name="font-style">italic</xsl:attribute>
      </xsl:if>
      <fo:table-cell>
        <fo:block>
          <xsl:value-of select="title"/>
        </fo:block>
      </fo:table-cell>
      <fo:table-cell>
        <fo:block>
          <xsl:value-of select="author"/>
        </fo:block>
      </fo:table-cell>
    </fo:table-row>
  </xsl:template>
</xsl:stylesheet>
