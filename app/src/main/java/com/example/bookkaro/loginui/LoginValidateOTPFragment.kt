package com.example.bookkaro.loginui

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.bookkaro.R
import com.example.bookkaro.databinding.FragmentLoginValidateOtpBinding
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_login_validate_otp.*
import java.util.concurrent.TimeUnit

class LoginValidateOTPFragment : Fragment() {

    private lateinit var storedVerificationId: String
    private lateinit var phone: String

    private lateinit var binding: FragmentLoginValidateOtpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_validate_otp, container, false)

        phone = requireArguments().getString("phone_number").toString()
        val otpMessage = getString(R.string.otp_sent_to) + " +91 $phone"
        binding.validateOtpSubHeader.text = otpMessage

        sendAuthRequest()

        binding.validateButton.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId, binding.loginOtpEdit.text.toString())
            signInWithPhoneAuthCredential(credential)
        }

        return binding.root
    }

    private fun sendAuthRequest() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Snackbar.make(binding.validateOtpCoordinator, R.string.verification_failed, Snackbar.LENGTH_SHORT).show()
                e.printStackTrace()
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                Log.d("dsdas", "verification code sent")
                storedVerificationId = verificationId
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91$phone",
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callbacks)

        setWaitingForOTP()

        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.codeNotReceivedWaitText.text = getWaitTime(millisUntilFinished)
            }

            override fun onFinish() {
                setRequestAgain()
            }
        }.start()

    }

    private fun getWaitTime(milli: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milli)
        return if(seconds <= 9)
            "Wait for 0:0$seconds"
        else
            "Wait for 0:$seconds"
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Navigation.findNavController(binding.validateButton).navigate(R.id.action_loginValidateOTPFragment_to_loginEnterDetailsFragment)
            } else {
                Snackbar.make(binding.validateOtpCoordinator, R.string.verification_failed, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setWaitingForOTP() {
        binding.codeNotReceivedWaitText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextSubHeader))
        binding.codeNotReceivedWaitText.setOnClickListener{}
    }

    private fun setRequestAgain() {
        binding.codeNotReceivedWaitText.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextHeader))
        binding.codeNotReceivedWaitText.text = getString(R.string.resend_otp)
        binding.codeNotReceivedWaitText.setOnClickListener {
            sendAuthRequest()
        }
    }

}