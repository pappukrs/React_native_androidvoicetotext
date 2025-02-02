import { NativeModules, NativeEventEmitter } from 'react-native';

const { VoiceToText } = NativeModules;
const eventEmitter = new NativeEventEmitter(VoiceToText);

export default {
  startListening: () => VoiceToText.startListening(),
  stopListening: () => VoiceToText.stopListening(),
  addListener: (eventName, callback) => {
    const subscription = eventEmitter.addListener(eventName, callback);
    return subscription.remove;
  },
};