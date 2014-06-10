(ns mail.css
  (:import [org.fit.cssbox.demo ComputeStyles]
           [org.fit.cssbox.css CSSNorm DOMAnalyzer DOMAnalyzer$Origin NormalOutput]
           [org.fit.cssbox.io StreamDocumentSource DefaultDOMSource]
           [java.net URL]
           [java.io ByteArrayInputStream ByteArrayOutputStream PrintStream]
           [java.nio.charset StandardCharsets]
           [org.w3c.dom Document]))

(defn inline
  [html]
  (let [;; Create the document source from a string.
        doc-source (StreamDocumentSource.
                    (ByteArrayInputStream. (.getBytes html StandardCharsets/UTF_8))
                    (URL. "https://")
                    "text/html")

        ;; Parse the input document.
        doc (-> doc-source
                DefaultDOMSource.
                .parse)

        ;; Create the CSS analyzer.
        da (doto (DOMAnalyzer. doc (.getURL doc-source))
             .attributesToStyles   ; convert the HTML presentation attributes to inline styles

             ;; Way to insert styles.
             ;; (.addStyleSheet nil (CSSNorm/stdStyleSheet) DOMAnalyzer$Origin/AGENT) ; use the standard style sheet
             ;; (.addStyleSheet nil (CSSNorm/userStyleSheet) DOMAnalyzer$Origin/AGENT) ; use the additional style sheet

             .getStyleSheets  ; load the author style sheets
             )

        baos (ByteArrayOutputStream.)
        ]

    ;; Compute the styles.
    (.stylesToDomInherited da)

    ;; Convert back to a string.
    (-> doc
        (NormalOutput.)
        (.dumpTo (PrintStream. baos)))

    ;; put in finally
    (.close baos)
    (.close doc-source) 

    (.toString baos "UTF-8")
    ))

#_ (time (inline ""))
