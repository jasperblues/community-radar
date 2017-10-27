package kudos.api.service.kudos

import kudos.api.service.twitter.TwitterClient
import kudos.domain.model.persistent.entities.pojo.Kudos
import kudos.domain.model.persistent.entities.pojo.User
import kudos.repositories.bolt.KudosRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL

@Service
class KudosService(val kudosRepository: KudosRepository,
                   val twitterClient: TwitterClient,
                   @Value("@{kudos.defaultImageUrl}") defaultImageUrl: String,
                   @Value("@{kudos.defaultDescription}") defaultDescription: String) {

    private val defaultImageUrl: URL
    private val defaultDescription : String

    init {
        try {
            this.defaultImageUrl = URL(defaultImageUrl)
        } catch (e: MalformedURLException) {
            throw IllegalStateException("A valid default image URL is required: $defaultImageUrl")
        }
        this.defaultDescription = defaultDescription
        if (this.defaultDescription.replace("@{kudos.defaultDescription}", "").isBlank()) {
            throw IllegalStateException("A default description is required: $defaultDescription")
        }
    }

    fun getRandom(): Kudos {
        val kudos = kudosRepository.getRandom()
        return when (kudos) {
            null -> throw IllegalArgumentException("No random kudos available.")
            else -> enrich(kudos)
        }
    }

    fun getByScreenName(id: String): Kudos {
        val kudos = kudosRepository.getByTwitterId(id)
        return when (kudos) {
            null -> throw IllegalArgumentException("No kudos for id $id")
            else -> enrich(kudos)
        }
    }

    /**
     * Add missing biography details, and a default image URL.
     */
    private fun enrich(kudos: Kudos): Kudos =
            kudos.copy(communityMember = twitterClient.load(kudos.communityMember.screenName)
                    .withDefaultImageURL(defaultImageUrl)
                    .withDefaultDescription(defaultDescription))
}

/**
 * Add the specified image URL, if currently null.
 */
fun User.withDefaultImageURL(url: URL) = when {
    this.imageUrl == null -> this.copy(imageUrl = url)
    else -> this
}

/**
 * Add the specified default description, if currently empty.
 */
fun User.withDefaultDescription(default: String) = when {
    this.description.isNullOrEmpty() -> this.copy(description = default)
    else -> this
}