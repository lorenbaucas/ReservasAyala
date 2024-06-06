package com.reservasayala.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.iamageo.library.BeautifulDialog
import com.iamageo.library.description
import com.iamageo.library.onNegative
import com.iamageo.library.onPositive
import com.iamageo.library.position
import com.iamageo.library.title
import com.iamageo.library.type
import com.reservasayala.R
import com.reservasayala.controllers.LoginActivity
import com.reservasayala.utils.FirebaseRD
import com.reservasayala.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {
    // Declaracion de variables
    private var userName: String? = null
    private lateinit var txtUsername: TextView
    private lateinit var etUsername: EditText
    private lateinit var btDeleteAccount: Button
    private lateinit var btDeleteHistory: Button
    private lateinit var btSettingsProfile: ImageButton
    private lateinit var btSettingsProfileCheck: ImageButton
    private lateinit var btSettingsProfileImage: ImageButton
    private lateinit var btLogout: ImageButton
    private lateinit var btSupport: ImageButton
    private lateinit var profileImage: CircleImageView
    private lateinit var dialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtUsername = view.findViewById(R.id.txtUsername)
        etUsername = view.findViewById(R.id.etUsername)

        btSettingsProfile = view.findViewById(R.id.btn_profile_settings)
        btSettingsProfileCheck = view.findViewById(R.id.btn_profile_settings_check)
        btSupport = view.findViewById(R.id.support_button)
        btLogout = view.findViewById(R.id.logout_button)
        btDeleteHistory = view.findViewById(R.id.btn_delete_history)
        btDeleteAccount = view.findViewById(R.id.btn_delete_account)
        btSettingsProfileImage = view.findViewById(R.id.btn_edit_image)

        val user = FirebaseAuth.getInstance().currentUser
        userName = user?.displayName
        if (userName == null) {
            val email = user?.email
            if (email != null) {
                userName = email.substringBefore("@")
                // Crear un UserProfileChangeRequest
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .build()

                // Actualizar el perfil del usuario
                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Utils.Toast(requireContext(), getString(R.string.error))
                        }
                    }
            }
        }
        // Carga la foto de perfil del usuario
        profileImage = view.findViewById(R.id.profile_image)
        if (user?.photoUrl != null) {
            Glide.with(this)
                .load(user.photoUrl)
                .into(profileImage)
        } else {
            Glide.with(this)
                .load(R.drawable.icon)
                .into(profileImage)
        }

        // Se pone el nombre del usuario
        txtUsername.text = userName
        etUsername.setText(userName)

        // Cambia la visibilidad de los botones para poder usarlos y hacer cambios en el perfil
        btSettingsProfile.setOnClickListener {
            btSettingsProfile.visibility = View.GONE
            txtUsername.visibility = View.GONE
            etUsername.visibility = View.VISIBLE
            btSettingsProfileCheck.visibility = View.VISIBLE
            btSettingsProfileImage.visibility = View.VISIBLE
            btDeleteAccount.visibility = View.VISIBLE
            btDeleteHistory.visibility = View.VISIBLE
        }

        // Al realizar cambios en el perfil se confirman al pulsar en el botón del icono check
        btSettingsProfileCheck.setOnClickListener {
            // Si el nombre es distinto lo actualiza y también muestra las demás opciones como
            // borrar cuenta, borrar historial o cambiar la foto de perfil
            if (etUsername.text.toString() != userName) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(etUsername.text.toString())
                    .build()

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Actualizar el valor de userName
                            userName = user.displayName
                            Utils.Toast(requireContext(), getString(R.string.updated_profile))
                            txtUsername.text = userName
                            etUsername.setText(userName)
                            changeVisibility()
                        } else {
                            Utils.Toast(requireContext(), getString(R.string.error))
                            changeVisibility()
                        }
                    }
            } else { changeVisibility() }
        }

        // Muestra ayudas por si el usuario tiene algunas dificultades
        btSupport.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.help_bottom_sheet, null)
            dialog = BottomSheetDialog(requireContext())
            dialog.setContentView(dialogView)
            dialog.show()
        }

        // Cierra la sesión actual del usuario
        btLogout.setOnClickListener {
            BeautifulDialog.build(requireActivity())
                .title(getString(R.string.logout_confirmation), titleColor = R.color.black)
                .description("")
                .type(type = BeautifulDialog.TYPE.NONE)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = getString(R.string.accept), shouldIDismissOnClick = true) {
                    FirebaseAuth.getInstance().signOut()
                    exit()
                }
                .onNegative(text = getString(R.string.cancel)) {}
        }

        // Elimina el historial de las reservas finalizadas y deja las pendientes
        btDeleteHistory.setOnClickListener {
            BeautifulDialog.build(requireActivity())
                .title(getString(R.string.are_you_sure), titleColor = R.color.black)
                .description("")
                .type(type = BeautifulDialog.TYPE.NONE)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = getString(R.string.accept), shouldIDismissOnClick = true) {
                    FirebaseRD().deleteBookingsHistory(user!!.uid)
                    Utils.Toast(requireContext(), getString(R.string.deleted_history))
                }
                .onNegative(text = getString(R.string.cancel)) {}
        }

        // Borra la cuenta permanentemente del usuario
        btDeleteAccount.setOnClickListener {

            BeautifulDialog.build(requireActivity())
                .title(getString(R.string.are_you_sure), titleColor = R.color.black)
                .description("")
                .type(type = BeautifulDialog.TYPE.NONE)
                .position(BeautifulDialog.POSITIONS.CENTER)
                .onPositive(text = getString(R.string.accept), shouldIDismissOnClick = true) {
                    BeautifulDialog.build(requireActivity())
                        .title(getString(R.string.are_you_sure).uppercase(), titleColor = R.color.black)
                        .description("")
                        .type(type = BeautifulDialog.TYPE.ALERT)
                        .position(BeautifulDialog.POSITIONS.CENTER)
                        .onPositive(text = getString(R.string.accept), shouldIDismissOnClick = true) {
                            user?.delete()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Utils.Toast(requireContext(), getString(R.string.deleted_account))
                                        exit()
                                    } else {
                                        Utils.Toast(requireContext(), getString(R.string.error))
                                    }
                                }
                        }
                        .onNegative(text = getString(R.string.cancel)) {}
                }
                .onNegative(text = getString(R.string.cancel)) {}
        }

        // Abre la galería para seleccionar una foto y actualizarla en el perfil
        btSettingsProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        /*
        Firebase.initialize(requireContext())
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        // Cargamos la foto de perfil
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                Glide.with(this)
                    .load(selectedImageUri)
                    .into(profileImage)
            }

            // Subir la imagen a Firebase Storage y luego actualizar la URL de la foto de perfil en Firebase Auth
            val storageRef = FirebaseStorage.getInstance().reference.child("profileImages")
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val uploadTask = storageRef.putFile(selectedImageUri!!)

            // Cambia la foto
            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()

                    FirebaseAuth.getInstance().currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Utils.Toast(requireContext(), getString(R.string.pp_updated))
                            } else {
                                // Si da error vuelve a la foto que tenía anteriormente
                                Utils.Toast(requireContext(), getString(R.string.error))
                                Glide.with(this)
                                    .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
                                    .into(profileImage)
                            }
                        }
                }
            }.addOnFailureListener { Utils.Toast(requireContext(), getString(R.string.error)) }
        }
    }

    // Cambia la visibilidad de los botones en los ajustes
    private fun changeVisibility() {
        txtUsername.visibility = View.VISIBLE
        etUsername.visibility = View.GONE
        btSettingsProfileCheck.visibility = View.GONE
        btSettingsProfileImage.visibility = View.GONE
        btSettingsProfile.visibility = View.VISIBLE
        btDeleteAccount.visibility = View.GONE
        btDeleteHistory.visibility = View.GONE
    }

    // Cierra la sesión del usuario y borra los ajustes de ese inicio de sesión para evitar entrar directamente
    private fun exit() {
        val prefs = requireContext()
            .getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        // Navega de vuelta a la actividad de inicio de sesión
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}