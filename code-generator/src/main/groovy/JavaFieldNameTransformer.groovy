class JavaFieldNameTransformer {
    static WellKnownWords = [
            'id', 'src', 'dst', 'pkg', 'order', 'type', 'doc', 'status', 'error', 'created',
            'processed', 'agent', 'gate', 'account', 'amount', 'currency', 'name', 'flags', 'cust',
            'release', 'link', 'ref', 'count', 'registry', 'replication', 'point', 'cc', 'bo', 'contract',
            'correspondent', 'authorization', 'time', 'sub', 'agent', 'comments', 'short', 'desc', 'next',
            'send', 'try', 'pan', 'client', 'log', 'notify', 'phone', 'number', 'in', 'queue', 'error', 'code',
            'text', 'email', 'user', 'comment', 'invoice', 'reply', 'technical', 'data', 'contract', 'sum',
            'uni', 'label', 'light', 'type', 'bank', 'operation', 'payment', 'time', 'operation', 'properties'
    ].toSet()

    String transform(String name) {
        def str = name.toLowerCase()
        def parts = extractParts(str)
        def builder = new StringBuilder()
        for (int i = 0; i < parts.size(); i++) {
            builder.append(i == 0 ? parts[i] : parts[i].substring(0, 1).toUpperCase() + parts[i].substring(1))
        }
        builder.toString()
    }

    Collection<String> extractParts(String str) {
        def res= []
        for (String part: str.split('_')) {
            res.addAll(toWords(part))
        }
        res
    }

    Collection<String> toWords(String str) {
        int index = 0
        def words = []
        def strRest = str
        while (index < str.length()) {
            boolean match = false
            for (String word: WellKnownWords) {
                if (strRest.startsWith(word)) {
                    words.add(word)
                    index += word.length()
                    strRest = index < str.length() ? str.substring(index) : ''
                    match = true
                    break
                }
            }
            if (!match) {
                break
            }
        }
        if (index < str.length()) {
            words.add(str.substring(index))
        }
        words
    }
}