package com.androidvoicetotext

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.bridge.UiThreadUtil

class VoiceToTextModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), RecognitionListener {

  private var speechRecognizer: SpeechRecognizer? = null

  override fun getName() = "VoiceToText"

  // Initialize speech recognizer
  init {
    UiThreadUtil.runOnUiThread {
      if (speechRecognizer == null) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(reactApplicationContext)
        speechRecognizer?.setRecognitionListener(this)
      }
    }
  }

  @ReactMethod
  fun startListening(promise: Promise) {
    UiThreadUtil.runOnUiThread {
      if (speechRecognizer == null) {
        promise.reject("NO_SPEECH_RECOGNIZER", "Speech recognizer not initialized")
        return@runOnUiThread
      }

      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, reactApplicationContext.packageName)
      }

      try {
        speechRecognizer?.startListening(intent)
        promise.resolve("Listening started")
      } catch (e: Exception) {
        promise.reject("LISTEN_ERROR", e.message)
      }
    }
  }

  @ReactMethod
  fun stopListening() {
    UiThreadUtil.runOnUiThread {
      speechRecognizer?.stopListening()
    }
  }

  // Speech recognition callbacks
  override fun onResults(results: Bundle?) {
    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
    matches?.let {
      sendEvent("onSpeechResults", Arguments.createArray().apply {
        for (match in matches) pushString(match)
      })
    }
  }

  override fun onPartialResults(partialResults: Bundle?) {
    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
    matches?.let {
      sendEvent("onSpeechPartialResults", Arguments.createArray().apply {
        for (match in matches) pushString(match)
      })
    }
  }

  override fun onError(error: Int) {
    sendEvent("onSpeechError", Arguments.createMap().apply {
      putString("error", "Error code: $error")
    })
  }

  private fun sendEvent(eventName: String, data: Any?) {
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, data)
  }

  // Other required RecognitionListener methods
  override fun onReadyForSpeech(params: Bundle?) {
    // Handle ready for speech event
  }

  override fun onBeginningOfSpeech() {
    // Handle beginning of speech event
  }

  override fun onRmsChanged(rmsdB: Float) {
    // Handle RMS change event
  }

  override fun onBufferReceived(buffer: ByteArray?) {
    // Handle buffer received event
  }

  override fun onEndOfSpeech() {
    // Handle end of speech event
  }

  override fun onEvent(eventType: Int, params: Bundle?) {
    // Handle event
  }
}