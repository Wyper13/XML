declare default element namespace "http://moviesRT";

<movies>
{
for $movie in collection("movies")//movie[matches(title, '^T', 'i')]
let $actors := count($movie/actors/actor)
order by $actors descending
return <movie><title>{$movie/title}</title>
<actors>{$actors}</actors></movie>
}
</movies>
