<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:mv="http://moviesRT">
	<xsl:output method="xml" version="1.0" indent="yes" omit-xml-declaration="yes"/>
	<xsl:param name="nActors" select="3"/>

	<xsl:template match="/">
		<table>
			<caption>
				Number of movies : <xsl:value-of select="count(//movie)"/>
			</caption>
			<head>
				<c>N</c>
        		<c>Title</c>
        		<c>Release Date</c>
        		<c>Directors</c>
        		<c>Actors</c>
        		<c>Vote average</c>
        		<c>Vote count</c>
			</head>
			<xsl:for-each select="//mv:movie">
				<xsl:sort select="mv:vote_average" data-type="number" order="descending"/>
				<xsl:sort select="mv:vote_count" data-type="number" order="descending"/>

				<row>
					<c><xsl:value-of select="position()"/></c>
					<c><xsl:value-of select="mv:title"/></c>
					<c><xsl:value-of select="mv:release_date"/></c>
					<c>
						<xsl:for-each select="mv:directors/mv:director">
							<xsl:choose>
								<xsl:when test="position() = 1">
									<xsl:value-of select="text()"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="concat(' ,', text())"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</c>
					<c>
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
					</c>
					<c><xsl:value-of select="mv:vote_average"/></c>
					<c><xsl:value-of select="mv:vote_count"/></c>
				</row>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>