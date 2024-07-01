from transformers import pipeline

class TextEmotionAnalysis:
    def __init__(self):
        self.emotion_pipeline = pipeline("text-classification", model="j-hartmann/emotion-english-distilroberta-base")
        self.translation_pipeline = pipeline("translation", model="Helsinki-NLP/opus-mt-ko-en")

    def analyze(self, text):
        translated_text = self.translation_pipeline(text)[0]['translation_text']
        results = self.emotion_pipeline(translated_text)
        return results
