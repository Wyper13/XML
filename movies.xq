declare default element namespace "http://moviesRT";

declare variable $number as xs:integer external := 10;
declare variable $average as xs:float external := 5;
declare variable $count as xs:integer external := 0;

<movies>
{
(for $movie in collection("movies")//movie[vote_average >= $average and vote_count >= $count]
let $vote_average := $movie/vote_average
let $vote_count := $movie/vote_count
order by xs:float($vote_average) descending, xs:integer($vote_count) descending
return <movie>{$movie/_id}{$movie/title}{$movie/release_date}
{$vote_average}{$vote_count}</movie>)[position() le $number]
}
</movies>
