package dev.bti.kdym.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.repositories.GroupRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val repo: GroupRepository = GroupRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _groups = MutableStateFlow<List<AppGroup>>(emptyList())
    val groups: StateFlow<List<AppGroup>> = _groups

    init {
        observeUserGroups()
    }

    private fun observeUserGroups() {
        viewModelScope.launch {
            auth.currentUser?.uid
                ?.let { uid ->
                    repo.getGroupsForUser(uid)
                        .collect { list ->
                            _groups.value = list
                        }
                }
        }

        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid ?: return@addAuthStateListener

            viewModelScope.launch {
                println("UID = ${auth.currentUser?.uid}")
                repo.getGroupsForUser(uid).collect { list ->
                    _groups.value = list
                }
            }
        }
    }
}