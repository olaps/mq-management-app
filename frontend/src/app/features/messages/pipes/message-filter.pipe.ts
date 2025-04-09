import { Pipe, PipeTransform } from '@angular/core';
import { Message } from '../../../core/models/message.model';

@Pipe({
  name: 'messageFilter'
})
export class MessageFilterPipe implements PipeTransform {
  transform(messages: Message[], searchTerm: string = ''): Message[] {
    if (!searchTerm.trim()) {
      return messages;
    }
    
    const lowerCaseSearch = searchTerm.toLowerCase();
    
    return messages.filter(message => 
      message.messageId.toLowerCase().includes(lowerCaseSearch) ||
      message.queueName.toLowerCase().includes(lowerCaseSearch) ||
      message.messageType.toLowerCase().includes(lowerCaseSearch) ||
      (message.content && message.content.toLowerCase().includes(lowerCaseSearch))
    );
  }
}