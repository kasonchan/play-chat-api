package models

/**
 * Created by kasonchan on 5/20/15.
 */

/**
 * User
 * @param login String
 * @param avatar_url String
 * @param user_type String
 * @param email String
 * @param location String
 * @param confirmed Boolean
 * @param created_at Long
 * @param updated_at Long
 */
case class User(login: String,
                avatar_url: String,
                user_type: String,
                email: String,
                location: String,
                confirmed: Boolean,
                created_at: Long,
                updated_at: Long)
