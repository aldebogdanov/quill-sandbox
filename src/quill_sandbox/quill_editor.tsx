import React, { useState, useRef, useEffect } from "react";
import ReactQuill from "react-quill";

type QuillEditorProps = {
  refFn: (rq: ReactQuill) => void;
  initialValue: string;
  onChange: (content: string) => void;
};

const QuillEditor: React.FC<QuillEditorProps> = ({
  refFn,
  initialValue,
  onChange,
}) => {
  const [content, setContent] = useState(initialValue);
  const quillRef = useRef<ReactQuill | null>(null);

  const handleChange = (value: string) => {
    setContent(value);
    onChange(value);
  };

  useEffect(() => {
    if (quillRef.current) {
      refFn(quillRef.current);
    }
  }, [quillRef]);

  return (
    <div>
      <ReactQuill ref={quillRef} value={content} onChange={handleChange} />
    </div>
  );
};

export default QuillEditor;
