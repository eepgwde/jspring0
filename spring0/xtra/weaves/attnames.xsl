<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text"/>
  <xsl:template match="@*">
    <xsl:value-of select="name(.)"/>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>
  <xsl:template match="*">
    <xsl:apply-templates select="@*"/>
    <xsl:apply-templates select="*"/>
  </xsl:template>
</xsl:stylesheet>
