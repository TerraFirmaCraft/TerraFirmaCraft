import json
import os

import Levenshtein


class I18n:

    lang: str

    def __init__(self, lang: str, validate: bool = False):
        self.lang = lang
        self.before = {}
        self.after = {}
        self.validate = validate
        self.lang_path = './resources/lang/%s.json' % lang

        self.fuzzy_matches = 0
        self.fuzzy_non_matches = 0
        
        # Default translation
        if not os.path.isfile(self.lang_path):
            if validate:
                raise ValueError('Cannot validate book for lang %s, as resources/lang/%s.json does not exist' % (lang, lang))
            print('Writing default translation for language %s to %s' % (self.lang, self.lang_path))
            with open(self.lang_path, 'w', encoding='utf-8') as f:
                f.write('{}\n')

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

    def is_root(self) -> bool:
        """ Return true if we are in the root language (en_us) """
        return self.lang == 'en_us'

    def translate(self, text: str) -> str:
        """ Translates the string into the current domain """
        if self.is_root():
            # For en_us, always keep the current text (read only)
            translated = text
        elif text in self.before:
            translated = self.before[text]  # Translate if available
        else:
            # Try a fuzzy matcher (if we're not in en_us)
            # Use the lowercase of both keys, as difference in capitalization is almost surely not a translation issue
            distance, match = min(((Levenshtein.distance(text.lower(), key.lower()), key) for key in self.before.keys()))
            if distance / len(text) < 0.1 and distance < 20:  # Heuristic: < 5% of text, and < 20 overall distance
                if self.before[match] == match:
                    # This has just matched a default key that was inserted in the translated files
                    # So if we slightly modify the en_us default, we should change this value as well.
                    self.fuzzy_non_matches += 1
                    translated = text
                else:
                    # Use the fuzzy match
                    self.fuzzy_matches += 1
                    translated = self.before[match]
            else:
                # Not available, but record and output anyway
                self.fuzzy_non_matches += 1
                translated = text

        self.after[text] = translated
        return translated

    def flush(self):
        """ Updates the local translation file, if needed """
        if not self.is_root() and self.fuzzy_matches + self.fuzzy_non_matches > 0:
            print('Matched %d / %d entries (%.1f%%). Updated %d entries for lang %s.' % (self.fuzzy_matches, self.fuzzy_matches + self.fuzzy_non_matches, 100 * self.fuzzy_matches / (self.fuzzy_matches + self.fuzzy_non_matches), self.fuzzy_non_matches, self.lang))
        if self.validate:
            assert self.before == self.after, 'Validation error translating book to lang \'%s\'' % self.lang
        with open(self.lang_path, 'w', encoding='utf-8') as f:
            unique_count = len(self.after) if self.is_root() else sum(k != v for k, v in self.after.items())
            print('Writing updated translation for language %s: %d / %d (%.2f%%)' % (self.lang, unique_count, len(self.after), 100 * unique_count / len(self.after)))
            json.dump(self.after, f, indent=2, ensure_ascii=False)

