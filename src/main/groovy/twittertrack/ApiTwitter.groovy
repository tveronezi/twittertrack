package twittertrack

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path('/twitter')
@Produces('application/json')
class ApiTwitter {

    @Inject
    private ServiceTwitter twitter

    @GET
    @Path('/{user}/{counter}')
    Collection<DtoTweet> listTweets(@PathParam("counter") Integer counter, @PathParam("user") String user) {
        return twitter.listTweets(user, counter)
    }

}
