package models

/**
 * Created by kasonchan on 5/20/15.
 */
case class User(login: String,
                avatar: String,
                user_type: String,
                email: String,
                location: String,
                confirmed: Boolean,
                created_at: Long,
                updated_at: Long)

