import os
import json


class I18n:

    lang: str

    def __init__(self, lang: str):
        self.lang = lang
        self.before = {}
        self.after = {}
        self.lang_path = './resources/lang/%s.json' % lang
        
        # Default translation
        if not os.path.isfile(self.lang_path):
            print('Writing default translation for language %s to %s' % (self.lang, self.lang_path))
            with open(self.lang_path, 'w', encoding='utf-8') as f:
                f.write('{}\n')

        if lang == 'en_us':
            return  # Don't read the en_us translation, it is only written to

        # Read the existing translation
        with open(self.lang_path, 'r', encoding='utf-8') as f:
            print('Reading translation for language %s to %s' % (self.lang, self.lang_path))
            j = json.load(f)

        # Parse json
        for key, value in j.items():
            if not isinstance(value, str):
                print('Illegal translation entry: "%s": "%s"' % (key, value))
                exit(-1)
            self.before[key] = value

    def translate(self, text: str) -> str:
        """ Translates the string into the current domain """
        if self.lang == 'en_us':  # For en_us, always keep the current text (read only)
            translated = text
        elif text in self.before:
            translated = self.before[text]  # Translate if available
        else:
            translated = text  # Not available, but record and output anyway

        self.after[text] = translated
        return translated

    def flush(self):
        """ Updates the local translation file, if needed """
        with open(self.lang_path, 'w', encoding='utf-8') as f:
            print('Writing updated translation for language %s' % self.lang)
            json.dump(self.after, f, indent=2, ensure_ascii=False)

