"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const react_1 = __importStar(require("react"));
const react_quill_1 = __importDefault(require("react-quill"));
const QuillEditor = ({ refFn, initialValue, onChange, }) => {
    const [content, setContent] = (0, react_1.useState)(initialValue);
    const quillRef = (0, react_1.useRef)(null);
    const handleChange = (value) => {
        setContent(value);
        onChange(value);
    };
    (0, react_1.useEffect)(() => {
        if (quillRef.current) {
            refFn(quillRef.current);
        }
    }, [quillRef]);
    return (react_1.default.createElement("div", null,
        react_1.default.createElement(react_quill_1.default, { ref: quillRef, value: content, onChange: handleChange })));
};
exports.default = QuillEditor;
