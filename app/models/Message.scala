package models

/**
 * Created by ka-son on 6/4/15.
 */


case class Message(owner: String,
                   room: Seq[String],
                   created_at: Long,
                   updated_at: Long)
