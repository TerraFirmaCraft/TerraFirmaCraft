import os
import json


class I18n:

    @staticmethod
    def create(lang: str):
        return I18n(lang) if lang == 'en_us' else ForLanguage(lang)

    lang: str

    def __init__(self, lang: str):
        self.lang = lang

    def translate(self, text: str) -> str:
        """ Translates the string into the current domain """
        return text

    def flush(self):
        """ Updates the local translation file, if needed """


class ForLanguage(I18n):
    def __init__(self, lang: str):
        super().__init__(lang)
        self.before = {}
        self.after = {}
        self.__lang_path = os.path.join(os.path.dirname(__file__), 'lang', self.lang + '.json')
        
        # Default translation
        if not os.path.isfile(self.__lang_path):
            print('Writing default translation for language %s to %s' % (self.lang, self.__lang_path))
            with open(self.__lang_path, 'w', encoding='utf-8') as f:
                f.write('{}\n')

        # Read the existing translation
        with open(self.__lang_path, 'r', encoding='utf-8') as f:
            print('Reading translation for language %s to %s'  % (self.lang, self.__lang_path))
            j = json.load(f)

        # Parse json
        for key, value in j.items():
            if not isinstance(value, str):
                print('Illegal translation entry: "%s": "%s"' % (key, value))
                exit(-1)
            self.before[key] = value

    def translate(self, text: str) -> str:
        if text in self.before:
            translated = self.before[text]  # Translate if available
        else:
            translated = text  # Not available, but record and output anyway

        self.after[text] = translated
        return translated

    def flush(self):
        with open(self.__lang_path, 'w', encoding='utf-8') as f:
            print('Writing updated translation for language %s' % self.lang)
            json.dump(self.after, f, indent=2, ensure_ascii=False)

