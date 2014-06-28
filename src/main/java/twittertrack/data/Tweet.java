package twittertrack.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Set;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Tweet implements Comparable<Tweet>, Serializable {

    @XmlElement
    private String id;

    @XmlElement
    private String content;

    @XmlElement
    private Long createdAt;

    @XmlElement
    private TweetUser user;

    @XmlElement
    private TweetUser author;

    @XmlElement
    private Set<TweetUser> mentions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public TweetUser getUser() {
        return user;
    }

    public void setUser(TweetUser user) {
        this.user = user;
    }

    public TweetUser getAuthor() {
        return author;
    }

    public void setAuthor(TweetUser author) {
        this.author = author;
    }

    public Set<TweetUser> getMentions() {
        return mentions;
    }

    public void setMentions(Set<TweetUser> mentions) {
        this.mentions = mentions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Tweet tweet = (Tweet) o;
        return this.id.equals(tweet.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public int compareTo(Tweet other) {
        return -1 * this.createdAt.compareTo(other.getCreatedAt());
    }
}
