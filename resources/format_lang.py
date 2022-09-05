import json


def main():
    en_us = load('en_us')
    for lang in ('zh_cn', 'ru_ru', 'ko_kr', 'pt_br', 'es_es', 'ja_jp'):
        format_lang(en_us, lang)


def format_lang(en_us, lang: str):
    lang_data = load(lang)

    formatted_lang_data = {}
    for k, v in lang_data.items():
        if '__comment' in k:
            formatted_lang_data[k] = v

    for k, _ in en_us.items():
        if k in lang_data:
            formatted_lang_data[k] = lang_data[k]

    print('%s: %d / %d' % (lang, len(lang_data), len(en_us)))
    save(lang, lang_data)


def load(lang: str):
    with open('./src/main/resources/assets/tfc/lang/%s.json' % lang, 'r', encoding='utf-8') as f:
        return json.load(f)


def save(lang: str, lang_data):
    with open('./src/main/resources/assets/tfc/lang/%s.json' % lang, 'w', encoding='utf-8') as f:
        json.dump(lang_data, f, ensure_ascii=False, indent=2)


if __name__ == '__main__':
    main()
