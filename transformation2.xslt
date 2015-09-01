<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:mv="http://moviesRT"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xhtml xsl xs mv">
	
	<xsl:output method="html"
        version="1.0"
        encoding="UTF-8"
        doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
        indent="yes"/>

	<xsl:template match="/">
	<html>
	<head>
		<link rel="stylesheet" href="stylesheet.css" type="text/css"/>
		<caption>Number of movies : <xsl:value-of select="count(//mv:movie)"/></caption>
	</head>
	<body>
		<table>			
			<header>
				<cell>N</cell>
        		<cell>Title</cell>
        		<cell>Release Date</cell>
        		<cell>Directors</cell>
        		<cell>Actors</cell>
        		<cell>Vote average</cell>
        		<cell>Vote count</cell>
			</header>
			<xsl:for-each select="//mv:movie">
				<xsl:sort select="mv:vote_average" data-type="number" order="descending"/>
				<xsl:sort select="mv:vote_count" data-type="number" order="descending"/>

				<row>
					<cell><xsl:value-of select="position()"/></cell>
					<cell><xsl:value-of select="mv:title"/></cell>
					<cell><xsl:value-of select="mv:release_date"/></cell>
					<cell>
						<xsl:for-each select="mv:directors/mv:director">
							<xsl:choose>
								<xsl:when test="position() = 1">
									<xsl:value-of select="text()"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(', ', text())"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</cell>
					<cell>
						<xsl:for-each select="mv:actors/mv:actor">
							<xsl:sort select="@cast_id" data-type="number" order="ascending"/>
							<xsl:choose>
								<xsl:when test="position() = 1">
									<xsl:value-of select="@name"/>
								</xsl:when>
								<xsl:when test="position() = 2">
									<xsl:value-of select="concat(', ', @name)"/>
								</xsl:when>
								<xsl:when test="position() = 3">
									<xsl:value-of select="concat(', ', @name)"/>
								</xsl:when>
							</xsl:choose>
						</xsl:for-each>
					</cell>
					<cell><xsl:value-of select="mv:vote_average"/></cell>
					<cell><xsl:value-of select="mv:vote_count"/></cell>
				</row>
			</xsl:for-each>
		</table>
	</body>
	</html>
	</xsl:template>
</xsl:stylesheet>