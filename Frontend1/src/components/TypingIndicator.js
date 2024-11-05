// import { concat, after, useListener } from 'polyrhythm';
// import { useState, useEffect } from 'react';

// const TypingIndicator = ({ timeout = 5000 }) => {
//   const [isTyping, setTyping] = useState(false);

//   // Define the autoTimeoutTyper function using concat and after
//   const autoTimeoutTyper = () =>
//     concat(
//       after(0, () => setTyping(true)), // Set typing to true immediately
//       after(timeout, () => setTyping(false)) // Set typing to false after the specified timeout
//     );

//   // Use the useListener hook to listen for specific events
//   useListener(/message\/edit\/(?!me)/, autoTimeoutTyper, { mode: 'replace' });
//   useListener(/message\/from/, () => setTyping(false));

//   // Render the TypingIndicator component if isTyping is true
//   return isTyping && <div className="chat-message__typing" />;
// };

// export default TypingIndicator;
