#!/usr/bin/env ruby

former_dict = File.new(ARGV[0], 'r')
small_dict = File.new(ARGV[1], 'w')

former_dict.each_line do |line|
  if line =~ /\?xml version=|<dictionary version=|<\/dictionary>|<grammeme|<\/grammeme|lemmata>/
    small_dict.puts line
  elsif line =~ /<lemma id=/ && rand < 0.0001
    small_dict.puts line
  end
end

former_dict.close
small_dict.close
