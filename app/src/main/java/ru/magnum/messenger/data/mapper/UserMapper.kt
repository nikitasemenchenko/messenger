package ru.magnum.messenger.data.mapper

import com.google.firebase.auth.FirebaseUser
import ru.magnum.messenger.domain.model.User

fun FirebaseUser.toDomain(): User {

    return User(
        id = uid,
        email = email.orEmpty()
    )

}