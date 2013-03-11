#!/usr/bin/env ruby

pos_array = %w{NOUN ADJF ADJS COMP VERB INFN PRTF PRTS GRND ADVB NUMR NPRO PRED PREP CONJ PRCL INTJ} # excluded due to low number
pos_hash = {}

pos_array.each do |pos|
  pos_hash[pos] = `grep '<lemma id=' #{ARGV[0]} | grep #{pos} | wc -l`.to_i
  puts "#{pos}\t#{pos_hash[pos]}"
end

pos_hash.select! { |k, v| v > 1000 }
puts pos_hash

former_dict = File.new(ARGV[0], 'r')
big_dict = File.new(ARGV[1], 'w')
small_dict = File.new(ARGV[2], 'w')

former_dict.each_line do |line|
  if line =~ /\?xml version=|<dictionary version=|<\/dictionary>|<grammeme|<\/grammeme|lemmata>/
    big_dict.puts line
    small_dict.puts line
  elsif line =~ /<lemma id=/
    from_array = false
    pos_hash.each do |k, v|
      if line[k]
        from_array = true
        if rand(v) < 1000
          small_dict.puts line
        else
          big_dict.puts line
        end
      end
    end
    unless from_array
      big_dict.puts line
      small_dict.puts line
    end
  end
end

former_dict.close
big_dict.close
small_dict.close